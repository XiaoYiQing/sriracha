/**
 * Created with IntelliJ IDEA.
 * User: yiqing
 * Date: 30/10/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Tests {

    public static void main(String[]args){

        String line = "I am a manly man.";

        String[] parameters = line.split("\\s+");

        for(int i = 0; i < parameters.length; i++)
            System.out.println(parameters[i]);

        System.out.println("Start index:" + line.indexOf("(") + "  End index:" + line.indexOf(")"));
        try{
            System.out.println(line.substring(line.indexOf("("),line.indexOf(")")).split("\\s+"));
        }catch(StringIndexOutOfBoundsException e){
            System.out.println("Something went wrong with splitting.");
        }

    }
}
