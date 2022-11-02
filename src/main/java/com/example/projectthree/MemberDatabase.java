package com.example.projectthree;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static com.example.projectthree.GymManagerController.*;

/**
 * The MemberDatabase is the class that will store the Member object. It is a sequenceList datastructures.
 * <p>
 * author: Michael Israel, Kangwei Zhu
 */
public class MemberDatabase {
    public static final int NOT_FOUND = -1;
    private static final int INCREMENT = 4;
    private Member[] mlist;
    private int size;

    /**
     * Initialize a newly created MemberDatabase object.
     * Initialize MemberDatabase's Member array mlist to an empty array with size 4. And set the number of members to 0.
     */
    public MemberDatabase() {
        mlist = new Member[INCREMENT];
        size = 0;
    }

    /**
     * This method is for finding whether a Member object exists in this MemberDatabase object.
     *
     * @param member a specific Member object.
     * @return Index of this Member in mlist if this Member does exist. If it not exists, return -1.
     */
    private int find(Member member) {
        for (int i = 0; i < size; i++) {
            if (mlist[i].equals(member)) {
                return i;
            }
        }
        return NOT_FOUND;
    }

    /**
     * This method is for check whether the MemberDataBase contains a specific Member object.
     *
     * @param member a specific Member object.
     * @return Index of this Member in mlist if this Member does exist. If it not exists, return -1.
     */
    public int contains(Member member) {
        return find(member);
    }

    /**
     * This method is used when retrieving a member from the database
     *
     * @param member The member that we want to retrieve.
     * @return null if not found, otherwise the Member Object.
     */
    public Member getMember(Member member) {
        int index = contains(member);
        if (index >= 0) {
            return mlist[index];
        } else {
            return null;
        }
    }

    /**
     * Increase the amount of Member object that the database could store by 4.
     */
    private void grow() {
        Member[] temp = new Member[mlist.length + INCREMENT];
        for (int i = 0; i < mlist.length; i++) {
            temp[i] = mlist[i];
        }
        mlist = temp;
    }

    /**
     * Get the number of Member object that currently stored in this database.
     *
     * @return number of Member object stored in this database.
     */
    public int getSize() {
        return size;
    }

    /**
     * Add a Member object into the database.
     * To successfully add a Member object, the database must not contain this Member object.
     *
     * @param member Member object that going to be added.
     * @return true if this Member is add successfully, false if this Member object has already been added.
     */
    public boolean add(Member member) {
        if (find(member) != NOT_FOUND) {
            return false;
        }
        mlist[size] = member;
        size++;
        if (size == mlist.length) {
            grow();
        }
        return true;
    }

    /**
     * Remove a Member object in database.
     * To remove this Member object, the database must have this object.
     *
     * @param member Member object that going to be removed.
     * @return true if this Member has been successfully removed, false otherwise.
     */
    public boolean remove(Member member) {
        int index = find(member);
        if (index == NOT_FOUND) {
            return false;
        } else {
            for (int i = index; (i + 1) < size; i++) {
                mlist[i] = mlist[i + 1];
            }
            mlist[size] = null;
            size--;
        }
        return true;
    }

    /**
     * Print all the Member objects that are currently stored in database
     */
    public void print() {
        if (size == 0) {
            System.out.println("Member database is empty!");
        } else {
            for (int i = 0; i < size; i++) {
                Member curMember = mlist[i];
                printByType(curMember);
                System.out.println();
            }
            System.out.println("-end of list-");
            System.out.println();
        }
    }

    /**
     * This method is used when displaying/printing the member by its membership Types.
     * This is because for Premium membership and Family membership, we need to print their number of guest pass.
     *
     * @param curMember The input member that are going to be displayed.
     */
    public void printByType(Member curMember) {
        if (curMember instanceof Premium) {
            System.out.print(curMember.toString() + ", (Premium) guest-pass remaining: " +
                    ((Premium) curMember).getNumOfGuestPass());
        } else if (curMember instanceof Family) {
            System.out.print(curMember.toString() + ", (Family) Guest-pass remaining: "
                    + ((Family) curMember).getNumOfGuestPass());
        } else {
            System.out.print(curMember.toString());
        }
    }

    /**
     * Print all the Member objects that are currently stored in database. Notice that this method is used for printing
     * the all the members in one specific fitness database.
     */
    public void printSchedule() {
        for (int i = 0; i < size; i++) {
            System.out.print("  ");
            printByType(mlist[i]);
            System.out.println();
        }
    }

    /**
     * Sort the database Member objects by county name, if county name is equal, then sort by zipcode.
     */
    public void printByCounty() {
        if (size == 0) {
            print();
        } else {
            System.out.println();
            System.out.println("-list of members sorted by county and zipcode-");
            for (int i = 1; i < size; i++) {
                for (int j = i; j > 0; j--) {
                    if (mlist[j - 1].getLocation().compareLocation(mlist[j].getLocation()) > 0) {
                        Member temp = mlist[j - 1];
                        mlist[j - 1] = mlist[j];
                        mlist[j] = temp;
                    } else {
                        break;
                    }
                }
            }
            print();
        }
    }

    /**
     * Sort the database Member objects by its expiration date.
     */
    public void printByExpirationDate() {
        if (size == 0) {
            print();
        } else {
            System.out.println();
            System.out.println("-list of members sorted by membership expiration date-");
            for (int i = size - 1; i > 0; i--) {
                for (int j = 0; j < i; j++) {
                    if (mlist[j].getExpire().compareTo(mlist[j + 1].getExpire()) > 0) {
                        Member temp = mlist[j];
                        mlist[j] = mlist[j + 1];
                        mlist[j + 1] = temp;
                    }
                }
            }
            print();
        }
    }

    /**
     * Sort the database Member objects by its first name and last name. First check first name, then check last name.
     */
    public void printByName() {
        if (size == 0) {
            print();
        } else {
            System.out.println();
            System.out.println("-list of members sorted by last name, and first name-");
            for (int i = size - 1; i > 0; i--) {
                for (int j = 0; j < i; j++) {
                    if (mlist[j].compareTo(mlist[j + 1]) > 0) {
                        Member temp = mlist[j];
                        mlist[j] = mlist[j + 1];
                        mlist[j + 1] = temp;
                    }
                }
            }
            print();
        }
    }

    /**
     * Print the current list with the membership Fees. (for next billing statement and regardless the expiration date)
     */
    public void printByMembershipFees() {
        if (size == 0) {
            print();
        } else {
            System.out.println("-list of members with membership fees-");
            for (int i = 0; i < size; i++) {
                Member currMember = mlist[i];
                printByType(currMember);
                System.out.println(", Membership fee: $" + currMember.membershipFee());
            }
            System.out.println("-end of list-\n");
        }
    }

    /**
     * This method handles "LM" command, which loads the historical member information from a text file to the member
     * database.
     */
    public void loadMembers() {
        String fileName = "memberList.txt";
        String[] lines = readFiles(fileName);
        System.out.println("\n-list of members loaded-");
        for (int i = 1; i < lines.length; i++) {
            String cmdLine = lines[i];
            String[] infos = cmdLine.split("\\s+");
            String firstName = infos[INDEX_OF_FIRSTNAME - 1];
            String lastName = infos[INDEX_OF_LASTNAME - 1];
            Date dob = new Date(infos[INDEX_OF_DOB - 1]);
            Date expireDate = new Date(infos[INDEX_OF_EXPIRATION_DATE - 1]);
            String location = infos[INDEX_OF_LOCATION + 1];
            Location newLocation = Location.valueOf(location.toUpperCase());
            /*if (isValidLocation(location)) {
                newLocation = Location.valueOf(location.toUpperCase());
                Member pastMember = new Member(firstName, lastName, dob, expireDate, newLocation);
                add(pastMember);
                System.out.println(pastMember.toString());
            }*/
            Member pastMember = new Member(firstName, lastName, dob, expireDate, newLocation);
            add(pastMember);
        }
        System.out.println("-end of list-\n");
    }

    /**
     * This method is used when reading lines from text file.
     *
     * @param fileName The name of file
     * @return The String array that contains all the lines. Each element in this array is the one line in the text file.
     */
    private String[] readFiles(String fileName) {
        File inputFile = new File(fileName);
        try {
            Scanner sc = new Scanner(inputFile);
            String line;
            int countNumOfLine = 0;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line == null || line.length() == 0) {
                    break;
                }
                countNumOfLine++;
            }
            String[] lines = new String[countNumOfLine + 1];
            lines[0] = Integer.toString(countNumOfLine);
            sc = new Scanner(inputFile);
            for (int i = 1; i < lines.length; i++) {
                lines[i] = sc.nextLine();
            }
            return lines;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}