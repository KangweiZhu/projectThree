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

import static com.example.projectthree.Date.VALIDAGE;

public class GymManagerController {
    protected static final int INDEX_OF_CLASS_NAME = 0;
    protected static final int MODE_C = 0;
    protected static final int MODE_D = 1;
    protected static final int MODE_CG = 2;
    protected static final int MODE_DG = 3;
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
        doCheckIn(newMember, location, addType);
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
        if (memberDB.getSize() != 0) {
            infoCenterTextField.appendText("-list of members-\n");
        }
        memberDB.getDbWarning().clear();
        memberDB.print();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }


    @FXML
    void viewByName(ActionEvent event) {
        memberDB.printByName();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    @FXML
    void viewByCounty(ActionEvent event) {
        memberDB.printByCounty();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    @FXML
    void viewByExpirationDate(ActionEvent event) {
        memberDB.printByExpirationDate();
        displayInfo(memberDB.getDbWarning(), infoCenterTextField);
    }

    @FXML
    void viewByMembershipFees(ActionEvent event) {
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
        fileChooser.setTitle("choose " + fileName + " from your file system");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        Stage stage = new Stage();
        File sourceFile = fileChooser.showOpenDialog(stage);
        return sourceFile;
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
    private void doCheckIn(Member newMember, String location, int addType) {
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
        if (checkDB(newMember)) {
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
    private boolean checkDB(Member member) {
        if (memberDB.contains(member) < 0) {
            if (isValidDob(member.getDob())) {
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
    public boolean isValidDob(Date date) {
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
        outputText.appendText("-----------------------------------------------\n" + "Line " + countAttempts + ":  " + "\n");
        countAttempts++;
    }

    private void displayInfo(ArrayList<String> alst, TextArea textArea) {
        for (int i = 0; i < alst.size(); i++) {
            textArea.appendText(alst.get(i) + "\n");
        }
        textArea.appendText("\n");
    }
}
