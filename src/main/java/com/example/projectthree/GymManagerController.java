package com.example.projectthree;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import static com.example.projectthree.Date.VALIDAGE;

public class GymManagerController {
    protected static final int INDEX_OF_CLASS_NAME = 0;

    protected static final int INDEX_OF_INSTRUCTOR = 1;
    protected static final int INDEX_OF_FIRSTNAME = 1;
    protected static final int INDEX_OF_LASTNAME = 2;
    protected static final int INDEX_OF_DAYTIME = 2;
    protected static final int INDEX_OF_DOB = 3;
    protected static final int INDEX_OF_LOCATION = 3;
    protected static final int MEMBER_AND_FAMILY_EXPIRE = 3;
    protected static final int INDEX_OF_EXPIRATION_DATE = 4;
    protected static final int INDEX_OF_CHECKIN_FNAME = 4;
    protected static final int INDEX_OF_CHECKIN_LNAME = 5;
    protected static final int INDEX_OF_CHECKIN_DOB = 6;

    MemberDatabase memberDB = new MemberDatabase();
    ClassSchedule classSchedule = new ClassSchedule();
    private int countAttempts;
    @FXML
    private TextField fnameTextField;
    @FXML
    private TextField lnameTextField;
    @FXML
    private TextField dobTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private RadioButton standard;
    @FXML
    private RadioButton family;
    @FXML
    private RadioButton premium;
    @FXML
    private TextArea outputText;
    @FXML
    private TextArea infoCenterTextField;
    @FXML
    private TextArea fitnessClassInfos;
    @FXML
    private TextField className;
    @FXML
    private TextField instructorName;
    @FXML
    private TextField memberFname;
    @FXML
    private TextField memberLname;
    @FXML
    private TextField dobFit;
    @FXML
    private TextField loc;
    @FXML
    private RadioButton memberCheckIn;
    @FXML
    private RadioButton memberDrop;
    @FXML
    private RadioButton guestCheckIn;
    @FXML
    private RadioButton guestDrop;

    public GymManagerController() {
        this.countAttempts = 1;
    }

    /**
     * This method is called when the "Add" button is clicked in the javafx GUI.
     * It will read the first name, last name, date of birth, registration location and Membership type from the
     * Text Field, then create a Member object and do the checks before add this object to the member Database.
     */
    @FXML
    void clickAdd(ActionEvent event) {
        printCountAttempts();
        String fname = fnameTextField.getText();
        String lname = lnameTextField.getText();
        String stringDob = dobTextField.getText();
        String location = locationTextField.getText();
        displayErrorMsg(fname, "fname");
        displayErrorMsg(lname, "lname");
        displayErrorMsg(stringDob, "stringDob");
        displayErrorMsg(location, "location");
        int addType;
        if (standard.isSelected()) {
            addType = 0;
        } else if (family.isSelected()) {
            addType = 1;
        } else if (premium.isSelected()) {
            addType = -1;
        } else {
            outputText.appendText("Please select something!\n");
            return;
        }
        Date dob = new Date(stringDob);
        Member newMember = new Member(fname, lname, dob);
        doCheckIn(newMember, location, addType, outputText);
    }

    @FXML
    void loadClassScheduleList(ActionEvent event) {
        printCountAttempts();
        File textClassSchedule = importFile("classSchedule");
        if (classSchedule != null) {
            outputText.appendText("The absolute path of the file you choose is: "
                    + textClassSchedule.getAbsolutePath() + "\n");
            classSchedule.LS(textClassSchedule);
            displayInfo(classSchedule.getWarning(), outputText);
        }
    }

    @FXML
    void clickRemove() {
        if (memberDB.getSize() == 0) {
            outputText.appendText("Member Database is empty!\n");
        } else {
            String fname = fnameTextField.getText();
            String lname = lnameTextField.getText();
            String stringDob = dobTextField.getText();
            Date date = new Date(stringDob);
            Member member = new Member(fname, lname, date);
            if (memberDB.remove(member)) {
                outputText.appendText(member.getFname() + " " + member.getLname() + " removed.\n");
            } else {
                outputText.appendText(member.getFname() + " " + member.getLname() + " is not in the database.\n");
            }
        }
    }

    @FXML
    void clearArea(ActionEvent event) {
        fitnessClassInfos.clear();
    }

    @FXML
    void fitnessClassTransactions(ActionEvent event) {
        String fName = memberFname.getText();
        String lName = memberLname.getText();
        String fitnessClassName = className.getText();
        String newlocation = loc.getText();
        Location location;
        Date dob = new Date(dobFit.getText());
        if (!isValidDob(dob, fitnessClassInfos)) {
            return;
        }
        String instructor = instructorName.getText();
        Member newMember = new Member(fName, lName, dob);
        if (memberDB.contains(newMember) >= 0) {
            newMember = memberDB.getMember(newMember);
        } else {
            fitnessClassInfos.appendText(fName + " " + lName + " " + dob + " is not in the database." + "\n");
            return;
        }
        if (isValidLocation(newlocation)) {
            location = Location.valueOf(newlocation.toUpperCase());
        } else {
            return;
        }
        FitnessClass fitnessClass = new FitnessClass(fitnessClassName, instructor, null, location);
        if (classSchedule.isFitnessClassExist(fitnessClass)) {
            fitnessClass = classSchedule.getFitnessClass(fitnessClass);
            if (fitnessClass == null) {
                return;
            }
            if (memberCheckIn.isSelected()) {
                doMemberCheckIn(fitnessClass, newMember);
            } else if (memberDrop.isSelected()) {
                doDrop(fitnessClass, newMember);
            } else if (guestCheckIn.isSelected()) {
                doDG(fitnessClass, newMember);
            } else if (guestDrop.isSelected()) {
                doCG(fitnessClass, newMember);
            }
        }else{
            displayInfo(classSchedule.getWarning(),fitnessClassInfos);
        }
    }

    @FXML
    void viewClassSchedule(ActionEvent event) {
        infoCenterTextField.clear();
        ArrayList<String> infos = new ArrayList<>();
        if (classSchedule.getNumClasses() == 0) {
            infoCenterTextField.appendText("Fitness class schedule is empty.\n");
        } else {
            for (int i = 0; i < classSchedule.getNumClasses(); i++) {
                classSchedule.getFitnessClasses()[i].printInfo();
                displayInfo(classSchedule.getFitnessClasses()[i].getFitnessClassInfos(), infoCenterTextField);
            }
        }
    }


    @FXML
    void loadMemberList(ActionEvent event) {
        printCountAttempts();
        File memberList = importFile("memberList");
        if (memberList != null) {
            outputText.appendText("The absolute path of the file you choose is: " + memberList.getAbsolutePath()
                    + "\n");
            memberDB.LM(memberList);
            displayInfo(memberDB.getDbWarning(), outputText);
        }
    }

    @FXML
    void defaultPrint(ActionEvent event) {
        infoCenterTextField.clear();
        if (memberDB.getSize() != 0) {
            infoCenterTextField.appendText("-list of members-\n");
        }
        memberDB.getDbWarning().clear();
        memberDB.print();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }


    @FXML
    void viewByName(ActionEvent event) {
        infoCenterTextField.clear();
        memberDB.printByName();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    @FXML
    void viewByCounty(ActionEvent event) {
        infoCenterTextField.clear();
        memberDB.printByCounty();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    @FXML
    void viewByExpirationDate(ActionEvent event) {
        infoCenterTextField.clear();
        memberDB.printByExpirationDate();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    @FXML
    void viewByMembershipFees(ActionEvent event) {
        infoCenterTextField.clear();
        memberDB.printByMembershipFees();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    /**
     * This method is used when you click the import button, it will pop up a new window and enable you to select the
     * file you want
     *
     * @param fileName The name of file. At here we only suppose to have memberList and classSchedule
     * @return
     */
    private File importFile(String fileName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose " + fileName + " from your file system");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage stage = new Stage();
        File sourceFile = fileChooser.showOpenDialog(stage);
        return sourceFile;
    }

    /**
     * This method is called when dropping a specific member from a specific fitnessClass.
     *
     * @param fitnessClass The fitness class that the member is going to be removed.
     * @param member       The member that are going to be removed.
     * @return True if successfully dropped, false otherwise.
     */
    private boolean doDrop(FitnessClass fitnessClass, Member member) {
        boolean flag = fitnessClass.drop(member);
        if (flag) {
            fitnessClassInfos.appendText(member.getFname() + " " + member.getLname() + " done with the class.\n");
        } else {
            fitnessClassInfos.appendText(member.getFname() + " " + member.getLname() + " did not check in.\n");
        }
        return flag;
    }

    private boolean doCG(FitnessClass fitnessClass, Member member) {
        int numOfPass = 0;
        boolean flag = false;
        if (member instanceof Family) {
            numOfPass = ((Family) member).getNumOfGuestPass();
            if (numOfPass == 0) {
                fitnessClassInfos.appendText(member.getFname() + " " + member.getLname() + " ran out of guest " +
                        "pass.\n");
            } else {
                if (member.getLocation().compareLocation(fitnessClass.getLocation()) == 0) {
                    ((Family) member).setNumOfGuestPass(-1);
                    flag = fitnessClass.addGuest(member);
                    fitnessClassInfos.appendText(member.getFname() + " " + member.getLname() + " (guest) checked in "
                            + fitnessClass.toString() + "\n");
                    fitnessClass.getFitnessClassInfos().clear();
                    fitnessClass.printSchedule();
                    displayInfo(fitnessClass.getFitnessClassInfos(), fitnessClassInfos);
                } else {
                    Location location = fitnessClass.getLocation();
                    String zipCode = location.getZipCode();
                    String county = location.getCounty().toUpperCase();
                    fitnessClassInfos.appendText(member.getFname() + " " + member.getLname() + " Guest checking in "
                            + location + ", " + zipCode + ", " + county + " - guest location restriction.\n");
                }
            }
        } else {
            fitnessClassInfos.appendText("Standard membership - guest check-in is not allowed.\n");
        }
        return flag;
    }

    /**
     * This method will check in a member into the memberDB
     *
     * @param newMember The Member object that will be checked in
     * @param location  The register location of that member going to be registered
     * @param addType   0 : Member Object
     *                  1: Family Object which extends Member Object
     *                  -1: Premium Object which extends Family Object
     */
    private void doCheckIn(Member newMember, String location, int addType, TextArea outputText) {
        String firstName = newMember.getFname();
        String lastName = newMember.getLname();
        Date dob = newMember.getDob();
        Location regisLocation = null;
        if (isValidLocation(location)) {
            regisLocation = Location.valueOf(location.toUpperCase());
        } else {
            return;
        }
        Date curDate = new Date();
        Date expireDate;
        if (addType == 0 || addType == 1) {
            if (curDate.checkNextYear(MEMBER_AND_FAMILY_EXPIRE) >= 0) {
                expireDate = new Date(curDate.checkNextYear(MEMBER_AND_FAMILY_EXPIRE) + "/" + curDate.getDay() + "/" +
                        (curDate.getYear() + 1));
            } else {
                expireDate = new Date(curDate.getMonth() + MEMBER_AND_FAMILY_EXPIRE + "/" + curDate.getDay() + "/" +
                        curDate.getYear());
            }
            if (addType == 0) {
                newMember = new Member(firstName, lastName, dob, expireDate, regisLocation);
            } else {
                newMember = new Family(firstName, lastName, dob, expireDate, regisLocation);
            }
        } else {
            expireDate = new Date(curDate.getMonth() + "/" + curDate.getDay() + "/" + (curDate.getYear() + 1));
            newMember = new Premium(firstName, lastName, dob, expireDate, regisLocation);
        }
        if (checkDB(newMember, outputText)) {
            memberDB.add(newMember);
            outputText.appendText(newMember.getFname() + " " + newMember.getLname() + " added.\n");
        }
    }

    /**
     * check whether a member is already in database or not
     *
     * @param member The member that are going to be checked.
     * @return True if not exist, otherwise false.
     */
    private boolean checkDB(Member member, TextArea outputText) {
        if (memberDB.contains(member) < 0) {
            if (isValidDob(member.getDob(), outputText)) {
                if (member.getExpire().isValidExpiration()) {
                    return true;
                } else {
                    outputText.appendText("Expiration date " + this.toString() + ": invalid calendar date!\n");
                }
            }
        } else {
            outputText.appendText(member.getFname() + " " + member.getLname() + " is already in the database.\n");
        }
        return false;
    }

    /**
     * This method is used when dropping a member's guest to a specific class
     *
     * @param fitnessClass The specific fitness class that the guest is going to be dropped.
     * @param guest        The guest
     * @return True if successfully dropped, false otherwise.
     */
    private boolean doDG(FitnessClass fitnessClass, Member guest) {
        boolean flag = fitnessClass.dropGuest(guest);
        if (flag) {
            fitnessClassInfos.appendText(guest.getFname() + " " + guest.getLname() + " Guest done with the class.\n");
        }
        return flag;
    }


    /**
     * The method is used to see if a fitness class located at a location.
     *
     * @param loc The location of a fitness class
     * @return true if there is a fitness class located at the location. false otherwise
     */
    private boolean isValidLocation(String loc) {
        for (Location location : Location.values()) {
            if (location.toString().equalsIgnoreCase(loc)) {
                return true;
            }
        }
        outputText.appendText(loc + ": invalid location!\n");
        return false;
    }

    /**
     * This method checks if a date of birth is valid.
     * Checks if older than 18, is a valid calendar date, not today or a future date
     *
     * @return true if date given is a valid dob, false otherwise
     */
    public boolean isValidDob(Date date, TextArea outputText) {
        Date currentDate = new Date();
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();
        int currentDateyear = currentDate.getYear();
        int currentDatemonth = currentDate.getMonth();
        int currentDateday = currentDate.getDay();
        if (!date.isValid()) {
            outputText.appendText("DOB " + date.toString() + ": invalid calendar date!\n");
            return false;
        }
        if (((year < (currentDateyear - VALIDAGE)) ||
                (year == (currentDateyear - VALIDAGE) && month < currentDatemonth) ||
                (year == (currentDateyear - VALIDAGE) && month == currentDatemonth) && day <= currentDateday)) {
            return true;
        } else if ((year == currentDateyear && month == currentDatemonth && day == currentDateday) ||
                (year == currentDateyear && month > currentDatemonth) ||
                (year == currentDateyear && month == currentDatemonth && day >= currentDateday) ||
                (year > currentDateyear)) {
            outputText.appendText("DOB " + date.toString() + ": cannot be today or a future date!\n");
        } else {
            outputText.appendText("DOB " + date.toString() + ": must be 18 or older to join!\n");
        }
        return false;
    }

    /**
     * This is the method that will print error message.
     * It is called when the input does not meet the requirement.
     *
     * @param empty empty String.
     * @param info  The place where the error happens.
     */
    private void displayErrorMsg(String empty, String info) {
        if ("".equals(empty)) {
            outputText.appendText("You forgot to add " + info + ".\n");
        }
    }

    /**
     * This is the method that will count how many times we make any actions(events)
     */
    private void printCountAttempts() {
        outputText.appendText("-----------------------------------------------\n" + "Line " + countAttempts + ":  "
                + "\n");
        countAttempts++;
    }

    private void displayInfo(ArrayList<String> alst, TextArea textArea) {
        for (int i = 0; i < alst.size(); i++) {
            textArea.appendText(alst.get(i) + "\n");
        }
        textArea.appendText("\n");
    }

    /**
     * This method is used when check in a member into a fitnessClass. It also does the checking before add the member into
     * the fitnessClass's student list.
     *
     * @param fitnessClass The fitness class this member wants to check in
     * @param member       The member that are going to be checked in
     * @return True if successfully checked in, false otherwise.
     */
    private boolean doMemberCheckIn(FitnessClass fitnessClass, Member member) {
        String fName = member.getFname();
        String lName = member.getLname();
        Location location = fitnessClass.getLocation();
        String zipCode = location.getZipCode();
        String county = location.getCounty().toUpperCase();
        boolean flag = false;
        if (!fitnessClass.isRegistered(member)) {
            if (!fitnessClass.isExpired(member)) {
                if (!isTimeConflict(fitnessClass, member)) {
                    if (member instanceof Family) {
                        fitnessClass.addMember(member);
                        fitnessClassInfos.appendText(fName + " " + lName + " checked in " +
                                fitnessClass.toString() + "\n");
                        //fitnessClass.getFitnessClassInfos().clear();
                        fitnessClass.printSchedule();
                        displayInfo(fitnessClass.getFitnessClassInfos(), fitnessClassInfos);
                    } else {
                        if (fitnessClass.getLocation().compareLocation(member.getLocation()) == 0) {
                            flag = fitnessClass.addMember(member);
                            fitnessClassInfos.appendText(fName + " " + lName + " checked in " +
                                    fitnessClass.toString() + "\n");
                            displayInfo(fitnessClass.getFitnessClassInfos(), fitnessClassInfos);
                        } else {
                            fitnessClassInfos.appendText(fName + " " + lName + " checking in " +
                                    location + ", " + zipCode + ", " + county +
                                    " - standard membership location restriction." + "\n");
                        }
                    }
                } else {
                    fitnessClassInfos.appendText("Time conflict - " + fitnessClass.toString() + ", " + zipCode + ", "
                            + county + "\n");
                }
            } else {
                fitnessClassInfos.appendText(fName + " " + lName + " " + member.getDob() +
                        " membership expired." + "\n");
            }
        } else {
            fitnessClassInfos.appendText(fName + " " + lName + " already checked in." + "\n");
        }
        return flag;
    }

    /**
     * Check if there is time conflict when a member want to check in a fitnessClass.
     * That is, if the member has already registered a fitness class that the time of the fitnessClass he wants to check
     * in. That is considered as time conflict.
     *
     * @param fitnessClass The fitness class that the mmember wants to check in.
     * @param member       The member that are going to check in a class
     * @return True if there is time conflict, false otherwise.
     */
    private boolean isTimeConflict(FitnessClass fitnessClass, Member member) {
        String classTime = fitnessClass.getTime().getDateTime();
        for (int i = 0; i < classSchedule.getNumClasses(); i++) {
            if (classSchedule.getFitnessClasses()[i].isRegistered(member)) {
                if (classTime.equals(classSchedule.getFitnessClasses()[i].getTime().getDateTime())) {
                    return true;
                }
            }
        }
        return false;
    }
}
