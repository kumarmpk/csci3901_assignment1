import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CourseSelector {

    private Map<Integer, List<String>> inputList = new HashMap<Integer, List<String>>();
    private String space = " ";
    private Integer ZERO = 0;

    /*
    removeSpace Method
    gets a single string
    remove unwanted space between courses and return list of strings
     */
    private ArrayList<String> removeSpace(String input){
        ArrayList<String> seperateCourseList = new ArrayList<>();

        //input string validation
        if(input != null && !input.isEmpty()) {
            for (String seperateCourse : Arrays.asList(input.toUpperCase().split(space))) {
                if (seperateCourse != null) {
                    seperateCourse = seperateCourse.replaceAll("\\s", "");  //removing unwanted tabs, new lines
                    if (!seperateCourse.isEmpty()) {    //validating course is a valid string
                        seperateCourseList.add(seperateCourse);
                    }
                }
            }
        }
        return seperateCourseList;
    }

    /*read method
    takes filename as input
    reads the input file, stores student info into a map and return of number of students
     */
    public Integer read( String fileName ){

        int noOfLines = ZERO;
        if(fileName != null && !fileName.isEmpty()) {       //filename validation
            try {
                inputList = new HashMap<>();
                Scanner scan = new Scanner(new File(fileName));

                while (scan.hasNextLine()) {
                    String lineContent = scan.nextLine().toUpperCase();
                    if (lineContent != null && !lineContent.isEmpty()) {    //line content validation
                        noOfLines = noOfLines + 1;
                        inputList.put(noOfLines, removeSpace(lineContent));
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Inputted file does not exist in the path");     //filepath related exceptions
            } catch (Exception e) {
                System.out.println("Unknown system error");         //all other exceptions are captured in this block
            }
        }
        return noOfLines;
    }

    /*recommend method
    gets taken course, support, recommendations as input
    return the recommendations as array list
     */
    public ArrayList<String> recommend( String taken, int support, int recommendations ) {
        ArrayList actualReturnList = null;

        //support & recommendations integer validation
        if(support <= ZERO || recommendations <= ZERO) {
            System.out.println("Support and recommendations cannot be less than ZERO or ZERO. Please correct the same");
            return null;
        }

        //read before recommend validation
        if(inputList == null || inputList.isEmpty()) {
            System.out.println("Requesting user to execute read command before recommend");
            return null;
        }

        //support & number of students validation
        if(support > inputList.size()){
            System.out.println("Support cannot be more than the number of students provided in input file");
            return null;
        }
        try {
            Map<String, Integer> returnMap = new TreeMap<String, Integer>();
            ArrayList returnList = new ArrayList();

            //inputted course validation
            if (taken != null && !taken.isEmpty()) {
                Map<Integer, List<String>> intersectMap = new ConcurrentHashMap<Integer, List<String>>();

                List<String> takenList = removeSpace(taken);
                int takenSize = takenList.size();

                //intersection between file data and method input is stored into a map
                for (String takenCourse : takenList) {
                    for (Integer mapKey : inputList.keySet()) {
                        if (!intersectMap.containsKey(mapKey)) {
                            List<String> inputCourses = inputList.get(mapKey);

                            if (inputCourses.contains(takenCourse)) {
                                List<String> dynamicList = intersectMap.get(mapKey);
                                if (dynamicList == null) {
                                    dynamicList = new ArrayList<>();
                                }
                                dynamicList.addAll(inputCourses);
                                intersectMap.put(mapKey, dynamicList);
                            }
                        }
                    }
                }

                //validation to have only the students with course combination
                if(takenSize > 1) {
                    Iterator<Integer> iterator = intersectMap.keySet().iterator();

                    while (iterator.hasNext()) {
                        Integer keyValue = iterator.next();
                        for (String takenCouse : takenList) {
                            if (!intersectMap.get(keyValue).contains(takenCouse)) {
                                intersectMap.remove(keyValue);
                                break;
                            }
                        }
                    }
                }

                //support is more than actual number of students with the course combination
                if (support > intersectMap.size()) {
                    return null;
                }

                //map is prepared with the course and the number of students taken with the combination
                for (Integer mapKey : intersectMap.keySet()) {
                    for (String course : intersectMap.get(mapKey)) {
                        if (!takenList.contains(course)) {
                            if (!returnMap.containsKey(course)) {
                                returnMap.put(course, 1);
                            } else {
                                returnMap.put(course, 1 + returnMap.get(course));
                            }
                        }
                    }
                }

                //sorting the map with the descending order of number of students
                Comparator<String> valueComparator =
                        new Comparator<String>() {
                            public int compare(String k1, String k2) {
                                int compare =
                                        returnMap.get(k2).compareTo(returnMap.get(k1));
                                if (compare == ZERO)
                                    return 1;
                                else
                                    return compare;
                            }
                        };

                Map<String, Integer> sortedByValues = new TreeMap<>(valueComparator);
                sortedByValues.putAll(returnMap);

                for (String course : sortedByValues.keySet()) {
                    returnList.add(course);
                }

                //list of courses to return
                if (returnList != null && !returnList.isEmpty()) {
                    actualReturnList = new ArrayList();
                    for (int i = 0; i < returnList.size(); i++) {
                        if (recommendations > i) {
                            actualReturnList.add(returnList.get(i));
                        }
                        // in case tie with recommendations
                        else {
                            if (returnMap.get(returnList.get(i)) == returnMap.get(returnList.get(i-1))){
                                actualReturnList.add(returnList.get(i));
                            }
                            else {
                                break;
                            }
                        }
                    }
                }

            } else {
                System.out.println("Please provide valid course in taken");
                return null;
            }
        } catch (Exception e) {
            System.out.println("we have encountered some unexpected exception");    //all exceptions are caught in this block
        }
        return actualReturnList;
    }

    /*
    getCoursesFromFile method
    returns all the courses from data file as list of strings
     */
    private List<String> getCoursesFromFile (){
        List<String> coursesFromFileList = null;

        if(inputList != null && !inputList.isEmpty()) {
            coursesFromFileList = new ArrayList<>();
            for (Integer lineNumber : inputList.keySet()) {     //loops each student
                for (String course : inputList.get(lineNumber)) {       //loops each course of a student
                    if (!coursesFromFileList.contains(course)) {
                        coursesFromFileList.add(course);
                    }
                }
            }
            Collections.sort(coursesFromFileList);
        }
        else {
            System.out.println("Read command is not executed or executed with wrong file");
        }
        return coursesFromFileList;
    }

    /*
    showCommon method
    takes course as string input
    prints the 2D matrix of courses and number of students with course combination
     */
    public boolean showCommon ( String courses ){
        try {
            List<String> methodInputCourseList = null;

            //input is validated
            if (courses != null && !courses.isEmpty()) {
                methodInputCourseList = new ArrayList<String>();
                methodInputCourseList.addAll(removeSpace(courses));
            } else {
                System.out.println("Please enter valid course");
                return false;
            }

            int methodInputCourseListSize = methodInputCourseList.size();

            List<String> coursesFromFileList = getCoursesFromFile();
            Set<String> newCourses = null;
            //prints 2D array
            if (coursesFromFileList != null && !coursesFromFileList.isEmpty()) {
                int twoDimArray[][] = getTwoDimArray();
                if (twoDimArray != null) {
                    for (int p = 0; p < methodInputCourseListSize; p++) {           //row wise looping
                        String rowMethodInputCourse = methodInputCourseList.get(p);
                        if (coursesFromFileList.contains(rowMethodInputCourse)) {
                            System.out.print(rowMethodInputCourse + "  ");
                            for (int q = 0; q < methodInputCourseListSize; q++) {       //column wise looping
                                String columnMethodInputCourse = methodInputCourseList.get(q);
                                if(coursesFromFileList.contains(columnMethodInputCourse)) {
                                    System.out.print(twoDimArray[coursesFromFileList.indexOf(rowMethodInputCourse)]
                                            [coursesFromFileList.indexOf(columnMethodInputCourse)] + " ");
                                }

                                //course inputted in the method is not in data file
                                else {
                                    if (newCourses == null) {
                                        newCourses = new HashSet<>();
                                    }
                                    newCourses.add(columnMethodInputCourse);
                                }
                            }
                            System.out.println();
                        } else {
                            if (newCourses == null) {
                                newCourses = new HashSet<>();
                            }
                            newCourses.add(rowMethodInputCourse);
                        }
                    }
                    if(newCourses != null && !newCourses.isEmpty()) {
                        System.out.println(newCourses + " : these courses are not in data file so not added in matrix");
                    }
                }
            } else {
                return false;
            }
        }
        catch (Exception e){
            System.out.println("System encountered unexpected exception");      //all exceptions are caught in this block
            return false;
        }
        return true;
    }

    /*
    getTwoDimArray method
    returns the 2D array for all the courses in data file
     */
    private int[][] getTwoDimArray() {

        List<String> coursesFromFileList = getCoursesFromFile();
        if(coursesFromFileList != null && !coursesFromFileList.isEmpty()){
            int coursesFromFileListSize = coursesFromFileList.size();
            int twoDimArray[][] = new int[coursesFromFileListSize][coursesFromFileListSize];

            for (String courseFromFile : coursesFromFileList) {     //loops all the courses from data file
                for (Integer mapKey : inputList.keySet()) {         //loops each student
                    if (inputList.get(mapKey).contains(courseFromFile)) {
                        for (String lineWiseCourse : inputList.get(mapKey)) {   //loops each course of a student
                            if (courseFromFile.equalsIgnoreCase(lineWiseCourse)) {
                                twoDimArray[coursesFromFileList.indexOf(courseFromFile)][coursesFromFileList.indexOf(lineWiseCourse)] = ZERO;
                            } else {
                                twoDimArray[coursesFromFileList.indexOf(courseFromFile)][coursesFromFileList.indexOf(lineWiseCourse)]++;
                            }
                        }
                    }
                }
            }
            return twoDimArray;
        }
        return null;
    }

    /*
    showCommonAll method
    gets the target file with path
    writes the 2D array into the file
     */
    public boolean showCommonAll(String fileName){

        //filename validation
        if(fileName != null && !fileName.isEmpty()) {
            try {
                List<String> coursesFromFileList = getCoursesFromFile();
                //read before showCommonAll validation
                if (coursesFromFileList != null && !coursesFromFileList.isEmpty()) {
                    int courseFromFileListSize = coursesFromFileList.size();
                    int twoDimArray[][] = getTwoDimArray();

                    if(twoDimArray != null) {

                        File file = new File(fileName);
                        FileWriter fileWriter = new FileWriter(file, false);    //to enable overwrite append is set as false
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        for (int p = 0; p < courseFromFileListSize; p++) {      //row wise loop
                            bufferedWriter.write(coursesFromFileList.get(p) + "  ");
                            for (int q = 0; q < courseFromFileListSize; q++) {      //column wise loop
                                bufferedWriter.write(twoDimArray[coursesFromFileList.indexOf(coursesFromFileList.get(p))]
                                        [coursesFromFileList.indexOf(coursesFromFileList.get(q))] + " ");
                            }
                            bufferedWriter.write("\n");     //prints new line
                        }
                        bufferedWriter.close();
                    }
                }
                else{
                    return false;
                }
            } catch (FileNotFoundException e) {
                System.out.println("File not found in the provided path");      //filepath exceptions are caught in this block
                return false;
            } catch (Exception e) {
                System.out.println("System encountered unexpected exception");      //all other exceptions are caught in this block
                return false;
            }
        }else{
            System.out.println("Inputted file name or path is not valid");
            return false;
        }
        return true;
    }

}
