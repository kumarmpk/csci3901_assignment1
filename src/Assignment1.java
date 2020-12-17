import java.util.*;

public class Assignment1 {

    // Retrieve data to the end of the line as an argument for a method call
    // Include two special kinds of arguments:
    //   "null" asks us to return no string
    //   "empty" asks us to return an empty string

    private static String getEndingString(Scanner userInput ) {
        String userArgument = null;

        userArgument = userInput.nextLine();
        userArgument = userArgument.trim();

        // Include a "hack" to provide null and empty strings for testing
        if (userArgument.equalsIgnoreCase("empty")) {
            userArgument = "";
        } else if (userArgument.equalsIgnoreCase("null")) {
            userArgument = null;
        }

        return userArgument;
    }

    // Main program to process user commands.
    // This method is not robust.  When it asks for a command, it expects all arguments to be there.
    // It is a quickly-done test harness rather than a full solution for an assignment.

    public static void main(String[] args) {
        // Command options

        String readCommand = "read";
        String recommendCommand = "recommend";
        String showCommonCommand = "show";
        String showCommonAllCommand = "all";
        String quitCommand = "quit";

        // Define variables to manage user input

        String userCommand = "";
        String userArgument = "";
        Scanner userInput = new Scanner( System.in );

        // Define the recommender that we will be testing.

        CourseSelector studentBody = new CourseSelector();

        // Define variables to catch the return values of the methods

        boolean booleanOutcome;
        ArrayList<String> recommendations = null;
        Integer recordsRead;

        // Let the user know how to use this interface

        System.out.println("Commands available:");
        System.out.println("  " + readCommand + " <filename>");
        System.out.println("  " + recommendCommand + " <support> <recommendations> <list of courses taken>");
        System.out.println("  " + showCommonCommand + " <list of courses>");
        System.out.println("  " + showCommonAllCommand + " <filename>");
        System.out.println("  " + quitCommand);

        // Process the user input until they provide the command "quit"

        do {
            // Find out what the user wants to do
            userCommand = userInput.next();

            /* Do what the user asked for. */

            if (userCommand.equalsIgnoreCase(readCommand)) {
                // Get the parameters.

                userArgument = getEndingString( userInput );

                // Call the method

                recordsRead = studentBody.read( userArgument );
                System.out.println(userCommand + " \""+userArgument+"\" outcome " + recordsRead );
            } else if (userCommand.equalsIgnoreCase(recommendCommand)) {
                // Get the parameters

                int support = userInput.nextInt();
                int numRecommendations = userInput.nextInt();
                userArgument = getEndingString( userInput );

                // Call the method

                recommendations = studentBody.recommend( userArgument, support, numRecommendations );
                System.out.println(userCommand + " \""+userArgument+"\", " + support + ", " + numRecommendations + " outcome " );
                if (recommendations == null) {
                    System.out.println("  null string");
                } else {
                    for ( String course: recommendations ) {
                        System.out.println( "  " + course );
                    }
                }
            } else if (userCommand.equalsIgnoreCase(showCommonCommand)) {
                // Get the parameters.

                userArgument = getEndingString( userInput );

                // Call the method

                booleanOutcome = studentBody.showCommon( userArgument );
                System.out.println(userCommand + " \""+userArgument+"\" outcome " + booleanOutcome );
            } else if (userCommand.equalsIgnoreCase(showCommonAllCommand)) {
                // Get the parameters.

                userArgument = getEndingString( userInput );

                // Call the method

                booleanOutcome = studentBody.showCommonAll( userArgument );
                System.out.println(userCommand + " \""+userArgument+"\" outcome " + booleanOutcome );
            } else if (userCommand.equalsIgnoreCase(quitCommand)) {
                System.out.println ( userCommand );
            } else {
                System.out.println ("Bad command: " + userCommand);
            }
        } while (!userCommand.equalsIgnoreCase("quit"));

        // The user is done so close the stream of user input before ending.

        userInput.close();
    }
}
