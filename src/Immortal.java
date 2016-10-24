import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.lang.Process;
import java.lang.Runtime;
import java.lang.StringBuffer;
import java.lang.Thread;

import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FileUtils;

public class Immortal{
    public String[] dictionary = new String[] {"+", "*", "/", ".", " ", "-"};
    
    private String cmd;
    private File cell;
    
    private Immortal(String four, String file){
        InputStream is = null;
        try{
            cell = new File(file);
            cmd = "gforth -e " + four.replace("~", " -e ") + " " + cell.getPath() + " -e bye";
            is = new FileInputStream(file);
            String m = IOUtils.toString(is, "UTF-8");
            int hscore = test(cmd, m);
            String hmut = m;
            for(int i = 0; i < 100; i++){
                String child = m;
                child = mutate(child);
                int s = test(cmd.replaceAll(cell.getPath(), child.replaceAll(" ", " -e ")), child);
                if(s > hscore){
                    hscore = s;
                    hmut = child;
                }
            }
            System.out.println(hmut + hscore);
            IOUtils.closeQuietly(is);
            FileUtils.deleteQuietly(cell);
            FileUtils.writeStringToFile(cell, hmut);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            IOUtils.closeQuietly(is);
        }
    }
    
    public static void main(String[] args){
        if(args.length >= 2){
            new Immortal(args[0], args[1]);
        }
        else{
            System.out.println("There must be two arguments: the setup FORTH command with spaces replaced with ~, and the Cell.");
        }
    }
    
    /**
     * Tests a command and returns the score. Command inputted through constructor.
     * @return Evolutionary score. 
     **/
    public int test(String cmd, String f){
        int score = 0;
        try{
            Process p = Runtime.getRuntime().exec(cmd);
            if(p.waitFor() != 0){
                score -= 1000;
            }
            String out = IOUtils.toString(p.getInputStream(), "UTF-8");
            if(out.equals("4 ")){
                score += 2000;
            }
            else if(out.length() > 0){
                score += 500;
            }
            //System.out.println(f);
            //score -= (f.length());
            p.getInputStream().close();
            p.destroy();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return score;
    }
    
    /**
     * Mutates a string in a random way using the dictionary.
     * @return Mutated string.
     **/
    public String mutate(String life){
        String out = life;
        Random r = new Random();
        StringBuffer s = new StringBuffer(out);
        int i = r.nextInt(out.length() + 1);
        switch(r.nextInt(2)){
            case 0: //Remove
                s.replace(i, i + (i < out.length() ? r.nextInt(out.length() - i) : 1), "");
                break;
            case 1: //Add
                s.insert(i, dictionary[r.nextInt(dictionary.length)]);
                break;
        }
        out = s.toString();
        Thread.sleep(0.1);
        return out;
    }
}