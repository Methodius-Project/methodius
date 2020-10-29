package uk.ac.ed.methodius;

public class TestMultipleUsers {


    public static void main(String[] args) {
        MethodiusEngine myEngine = new MethodiusEngine("/group/ltg/users/amyi/methodius/software/methodius/data/indigo-methodius.config");

        for (int i = 1; i < 5; i++) {
            String thisUser = "user" + i;
            System.out.println("user " + thisUser);
            System.out.println();
            
            String result = myEngine.generateDescription(thisUser, "exhibit44", "adult");
            System.out.println(result);
            System.out.println();
            
            String result11 = myEngine.generateDescription(thisUser, "exhibit44", "adult");
            System.out.println(result11);
            System.out.println();
            
            String result2= myEngine.generateDescription(thisUser, "exhibit18", "adult");
            System.out.println(result2);
            System.out.println();
                        
            String result3= myEngine.generateDescription(thisUser, "exhibit1", "adult");
            System.out.println(result3);
            System.out.println();
        }
    }
}
