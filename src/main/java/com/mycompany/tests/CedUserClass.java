/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.tests;

import com.mycompany.shoptester.MainJFrame;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/**
 *
 * @author user
 */
public class CedUserClass {
    
    private JavascriptExecutor js;
    private HelperClass helperClass = new HelperClass();
    private CredentialsClass credentialsClass;
    private String dateTimeOfSession;
    private String pathToLogFileFolder;
    private File fileToWriteLogsOfTesting;
    private File fileToWriteErrorLogOfTesting;
    private WebDriver webDriver = null;
    private String mainUrl = "http://shop.loc/";
    private String testUrl = "http://shop.loc/admin/users/list/";
    private String osName;
    private String firstName = "";
    private String secondName = "";
    private String lastName = "";
    private String fullName = "";
    private String loginU = "";
    private String emailToUse = "";
    private int userId = 0;
    
    public CedUserClass(String pathToFileFolderIn, String osNameIn){
        this.pathToLogFileFolder = pathToFileFolderIn;
        this.osName = osNameIn;
    }
    
    public void startCedTestUsers() {
        
        loginU = "" + helperClass.getRandomStringWithLength(12);
        firstName = "FN_" + helperClass.getRandomStringWithLength(14);
        secondName = "SN_" + helperClass.getRandomStringWithLength(14);
        lastName = "LN_" + helperClass.getRandomStringWithLength(14);
        emailToUse = helperClass.getRandomStringWithLength(6) + "@mail.com";
        
        //fullName = lastName + ", " + firstName;
        credentialsClass = new CredentialsClass();
        dateTimeOfSession = helperClass.getDateInStringForWindowsLinux();    
        String fileName = "";
        String fileNameERRORS = "";
        
        fileName = this.pathToLogFileFolder + "testUserCedLogFile_" + dateTimeOfSession + ".txt";
        fileNameERRORS = this.pathToLogFileFolder + "testUserCedLogFile_ERRORS_" + dateTimeOfSession + ".txt";        
        
        try {
            fileToWriteLogsOfTesting = new File(fileName);
            fileToWriteErrorLogOfTesting = new File(fileNameERRORS);
            System.out.println("Path to logfile:" + fileName);
        } catch (Exception exx) {
            System.out.println(exx.getMessage());
            System.out.println("Error file creation, test log will be only in terminal");
        }
        
        helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Candidate creation testing starts at: " + dateTimeOfSession +" OS: " + osName);
        
        try {
            if(MainJFrame.CURRENT_BROWSER == MainJFrame.CHANGE_CHROME_BROWSER) {
                webDriver = new ChromeDriver();
            } else {
                webDriver = new FirefoxDriver();
            }
            //login to site START
            js = (JavascriptExecutor)webDriver;
            webDriver.manage().window().maximize();
            helperClass.writeStringToFile(fileToWriteLogsOfTesting, "Work: go to url:" + mainUrl);
            webDriver.get(mainUrl);
            Thread.sleep(500);
            webDriver.findElement(By.cssSelector("#navbarSupportedContent > ul.navbar-nav.ml-auto > li:nth-child(1) > a")).click();
            Thread.sleep(500);
            WebElement login = webDriver.findElement(By.id("email"));
            WebElement passwd = webDriver.findElement(By.id("password"));
            WebElement btnLogin = webDriver.findElement(By.cssSelector("#app > main > div > div > div > div > div.card-body > form > div.form-group.row.mb-0 > div > button"));
            login.sendKeys(credentialsClass.emailToLogin);
            passwd.sendKeys(credentialsClass.passwordToLogin);
            Thread.sleep(300);            
            helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: trying to login with email " + credentialsClass.emailToLogin + " and pswd " + credentialsClass.passwordToLogin);
            btnLogin.click();
            Thread.sleep(500);  
            checkIfOnUrlNow("home");
            webDriver.findElement(By.id("adminConrol")).click();
            Thread.sleep(300); 
            webDriver.findElement(By.id("management")).click();
            Thread.sleep(300); 
            webDriver.findElement(By.id("managementUsers")).click();
            Thread.sleep(300); 
            webDriver.findElement(By.id("userCreateButton")).click();
            Thread.sleep(300);  
            checkIfOnUrlNow("user/create");
            Thread.sleep(500);  
            
            try {                
                fillUserDataAndSave();
                Thread.sleep(300); 
            } catch (Exception ex) {
                helperClass.writeErrorsToFiles(fileToWriteLogsOfTesting, fileToWriteErrorLogOfTesting, "ERROR: Unable to fill user data", ex.getMessage());
            }          
            
            Thread.sleep(500);            
            
            

            if (checkIfOnUrlNow("admin/users/list")) {
                try {                
                    tryToClickOnUserEditButton();
                    Thread.sleep(300); 
                } catch (Exception ex) {
                    helperClass.writeErrorsToFiles(fileToWriteLogsOfTesting, fileToWriteErrorLogOfTesting, "ERROR: Unable to check saving user data", ex.getMessage());
                } 
            } else {
                helperClass.writeStringToFile(fileToWriteLogsOfTesting, "Work: Error - not on page with users list");
            }                        
                        
            checkIfOnUrlNow("user/edit/" + userId);
            
            editSavedUserData();
            Thread.sleep(1500); 
            helperClass.writeStringToFile(fileToWriteLogsOfTesting, "Work: END");
            Thread.sleep(5000);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            helperClass.printToFileAndConsoleInformation(fileToWriteErrorLogOfTesting, "ERROR: Error in main try block of CedUserClass"); 
        } finally {
            webDriver.close();
            webDriver.quit();
        }
    }
    
    public boolean checkIfOnUrlNow(String url) {
        if(webDriver.getCurrentUrl().contains(mainUrl + url)) {
                helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: if on page \"" + mainUrl + url + "\" - success!\n");
                return true;
        } 
        
        helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "ERROR! Not on url " + mainUrl + url);
        return false;
        
    }

    private void fillUserDataAndSave() throws InterruptedException {
        helperClass.editDataInTextInputWithLabel(webDriver, loginU, "id", "login", "cssSelector", "#app > main > div > div > div > div > div > div.col-9.p-0.bg-secondary > main > div > div > div > div > div.d-flex.justify-content-between.flex-wrap > div > div.card-body > form > div:nth-child(3) > label", fileToWriteLogsOfTesting);
        Thread.sleep(200);  
        helperClass.editDataInTextInputWithLabel(webDriver, firstName, "id", "first_name", "cssSelector", "#app > main > div > div > div > div > div > div.col-9.p-0.bg-secondary > main > div > div > div > div > div.d-flex.justify-content-between.flex-wrap > div > div.card-body > form > div:nth-child(4) > label", fileToWriteLogsOfTesting);
        Thread.sleep(200); 
        helperClass.editDataInTextInputWithLabel(webDriver, secondName, "id", "second_name", "cssSelector", "#app > main > div > div > div > div > div > div.col-9.p-0.bg-secondary > main > div > div > div > div > div.d-flex.justify-content-between.flex-wrap > div > div.card-body > form > div:nth-child(5) > label", fileToWriteLogsOfTesting);
        Thread.sleep(200); 
        helperClass.editDataInTextInputWithLabel(webDriver, lastName, "id", "last_name", "cssSelector", "#app > main > div > div > div > div > div > div.col-9.p-0.bg-secondary > main > div > div > div > div > div.d-flex.justify-content-between.flex-wrap > div > div.card-body > form > div:nth-child(6) > label", fileToWriteLogsOfTesting);
        Thread.sleep(200); 
        helperClass.editDataInTextInputWithLabel(webDriver, emailToUse, "id", "email", "cssSelector", "#app > main > div > div > div > div > div > div.col-9.p-0.bg-secondary > main > div > div > div > div > div.d-flex.justify-content-between.flex-wrap > div > div.card-body > form > div:nth-child(6) > label", fileToWriteLogsOfTesting);
        Thread.sleep(200);
        helperClass.editDataInTextInputWithLabel(webDriver, "123456", "id", "password", "cssSelector", "#app > main > div > div > div > div > div > div.col-9.p-0.bg-secondary > main > div > div > div > div > div.d-flex.justify-content-between.flex-wrap > div > div.card-body > form > div:nth-child(7) > label", fileToWriteLogsOfTesting);
        Thread.sleep(200);
        webDriver.findElement(By.id("userCreateEditBtn")).click();
    }

    private void checkIfUserSaved() throws InterruptedException {        
        //js.executeScript("window.scrollBy(0,1000)");
        helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: ");      
    }

    private boolean findIdByLoginOnPage() throws InterruptedException {
        boolean isFound = false;
        Thread.sleep(500);
        WebElement tableWithUsers = helperClass.safeFindElement(webDriver, "tableWithUsersData", "id");
        List<WebElement> listUserTrs = null;
        List<WebElement> listOfInternalTds = null;
        
        try {
            listUserTrs = tableWithUsers.findElements(By.tagName("tr"));
        } catch (Exception ex) {
            helperClass.writeErrorsToFiles(fileToWriteLogsOfTesting, fileToWriteErrorLogOfTesting, "Error while finding tr", ex.getMessage());            
        }
        
        helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: try to find id of user with login:" + loginU);

        if (listUserTrs.size() > 1) {
            for (int i = 1; i < listUserTrs.size(); i++) {
                Thread.sleep(500);
                listOfInternalTds = listUserTrs.get(i).findElements(By.tagName("td"));
                helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: listOfInternalTds.get(1).getText()=" + listOfInternalTds.get(1).getText());

                if(listOfInternalTds.get(1).getText().contains(loginU)) {
                    userId = Integer.valueOf(listOfInternalTds.get(0).getText());
                    helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: resId=" + userId); 
                    isFound = true;
                }
            }
        }
        
        Thread.sleep(500);
        return isFound;
    }

    private void editSavedUserData() {
        helperClass.writeStringToFile(fileToWriteLogsOfTesting, "Work: Try to edit users data (editSavedUserData func)");
    }

    private void tryToClickOnUserEditButton() throws InterruptedException {
        
        boolean isUserFound = false;
        boolean isPaginationFound = false;
        boolean isWork = true;
        int numberOfPage = 0;
        List<WebElement> listOfPageLinks = webDriver.findElements(By.className("page-item"));
        if (listOfPageLinks != null) {
            if (listOfPageLinks.size() > 0) {                
                isPaginationFound = true;
            }
        }
        Thread.sleep(500);
        int counter = 1;
        if (isPaginationFound){
            do {
                webDriver.get(mainUrl + "admin/users/list?page=" + counter);
                Thread.sleep(500);
                js.executeScript("window.scrollBy(0,250)");
                Thread.sleep(500);
                WebElement tableWithUsers = helperClass.safeFindElement(webDriver, "tableWithUsersData", "id");
                List<WebElement> listUserTrs = null;
                List<WebElement> listOfInternalTds = null;

                try {
                    listUserTrs = tableWithUsers.findElements(By.tagName("tr"));
                } catch (Exception ex) {
                    helperClass.writeErrorsToFiles(fileToWriteLogsOfTesting, fileToWriteErrorLogOfTesting, "Error while finding tr", ex.getMessage());            
                }

                helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: try to find id of user with login:" + loginU);
                Thread.sleep(500);
                if (listUserTrs.size() > 1) {
                    for (int i = 1; i < listUserTrs.size(); i++) {
                        Thread.sleep(500);
                        listOfInternalTds = listUserTrs.get(i).findElements(By.tagName("td"));
                        helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: listOfInternalTds.get(1).getText()=" + listOfInternalTds.get(1).getText());

                        if(listOfInternalTds.get(1).getText().contains(loginU)) {
                            userId = Integer.valueOf(listOfInternalTds.get(0).getText());
                            helperClass.printToFileAndConsoleInformation(fileToWriteLogsOfTesting, "Work: resId=" + userId); 
                            isUserFound = true;
                            numberOfPage = counter;
                            isWork = false;
                        }
                    }
                } 
                Thread.sleep(500);
                counter++;
            } while (isWork);
        } else {
            isUserFound = findIdByLoginOnPage();
        }
        
        if(isUserFound) {
            webDriver.get(mainUrl + "admin/users/list?page=" + numberOfPage);
            Thread.sleep(500);
            WebElement editButton = webDriver.findElement(By.id("edit" + userId));
            editButton.click();
        }
        
    }
    
}