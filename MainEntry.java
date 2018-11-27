import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jdk.nashorn.internal.AssertsEnabled;
import utils.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class MainEntry extends Application{

    public Label label;
    private static BibaFileHandler bibaFileHandler;

    public static void main(String args[]) {
        System.out.println("hello world!");
        bibaFileHandler = BibaFileHandler.getInstance();
//
        User supervisor = bibaFileHandler.createNewUser(Hierarchy.LEVEL_SUPERVISOR,"C. Shen");
        User Alice = bibaFileHandler.createNewUser(Hierarchy.LEVEL_CLASSIFED, "Alice");

        try {
            Alice.createFile("\\hello\\world\\test.txt");
            supervisor.createFile("\\missu.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }


//        Map<String, User> map1 = new HashMap<>();
//        map1.put("C. Shen", supervisor);
//        map1.put("Alice", Alice);
//
//        try {
//            ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(new File(".\\tmp")));
//            o.writeObject(map1);
//            o.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Map<String, User> map2 = null;
//
//        try {
//            ObjectInputStream i = new ObjectInputStream(new FileInputStream(new File(".\\tmp")));
//            map2 = (Map<String, User>) i.readObject();
//            map2 = (map2 == null) ? new HashMap<>() : map2;
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//
//
//        ((Supervisor) map2.get("C. Shen")).grantUserNewLevel(map2.get("Alice"), Hierarchy.LEVEL_PUBLIC);
//        System.out.println("Test");
//        System.out.println(map2.size());

//        System.out.println(bibaFileHandler.getFilesTotal());
//        System.out.println(bibaFileHandler.getUsersTotal());
//        bibaFileHandler.cleanUp();

//        JFrame mainFrame = new JFrame("Biba Test");
//        mainFrame.setSize(640, 480);
//        mainFrame.setLocationRelativeTo(null);
//        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//
//        JPanel userPanel = new JPanel();
//
//        userPanel.setBackground(Color.BLUE);
//        userPanel.setLayout(new FlowLayout());
//        userPanel.add(new JLabel("Users"));
//
//
//        JPanel filePanel = new JPanel();
//        filePanel.setBackground(Color.CYAN);
//        filePanel.add(new JLabel("Files"));
//
//        Box box = Box.createHorizontalBox();
//
//        box.add(userPanel);
//        box.add(filePanel);
//
//        mainFrame.add(box);
//        mainFrame.setVisible(true);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource(".\\gui\\biba_ui.fxml"));
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

    }

    @Override
    public void stop() {
        bibaFileHandler.cleanUp();
    }
}
