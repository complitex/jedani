/**
 * @author Anatoly A. Ivanov
 * 26.06.2018 17:01
 */
public class InstanceTest {
    public static void main(String[] args) {
        String s1 = null;

        if(s1 instanceof String){
            System.out.println("1. String");
        }else{
            System.out.println("1. Not String");
        }

        String s2 = "HELLO";

        if(s2 instanceof String){
            System.out.println("2. String");
        }else{
            System.out.println("2. Not String");
        }
    }

}
