import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import utils.BibaFile;
import utils.BibaFileHandler;
import utils.RuleConflictException;
import utils.User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class BibaUiController {
    public Button addNewUser;
    public Button addNewFile;
    public Button saveFile;
    public Button openFile;

    public Label currentFile;
    public Label currentUser;

    public ListView allUsers;
    public ListView allFiles;

    public TextArea inputBox;
    public ChoiceBox actionSelector;



    private BibaFileHandler bibaFileHandler;
    private int currentAction = BibaFile.ACCESS_TYPE_READ;

    private User curUser;
    private BibaFile curFile;

    public BibaUiController() {
        bibaFileHandler = BibaFileHandler.getInstance();

        currentFile = new Label();
        currentUser = new Label();

        allFiles = new ListView();
        allUsers = new ListView();

        inputBox = new TextArea();

        addNewFile = new Button();
        addNewUser = new Button();
        saveFile = new Button();
        openFile = new Button();

        actionSelector = new ChoiceBox();
    }

    @FXML
    private void initialize() {
        currentUser.setText("No selected user");
        currentUser.setText("No selected file");


        actionSelector.setItems(FXCollections.observableArrayList("Read", "Write", "Read & Write"));
        actionSelector.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // The number are same as action number defined in BibaFile.
            currentAction = newValue.intValue();
        });
        actionSelector.getSelectionModel().select(0);

        refreshFileList();
        refreshUserList();

        allUsers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            curUser = bibaFileHandler.getExistUser(String.valueOf(newValue));
            currentUser.setText("Name: " + ((String) newValue) + "\nLevel: " + String.valueOf(curUser.getLevel()));
        });

        allFiles.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            curFile = bibaFileHandler.getExistFile(String.valueOf(newValue));
            currentFile.setText("Path: " + ((String) newValue) + "\tLevel: " + String.valueOf(curFile.getLevel()));
        });

        openFile.setOnAction(event -> {
            File file;

            if ((file = getFileHandler()) == null) {
                return;
            }

            StringBuilder all = new StringBuilder("");

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null) {
                    all.append(st);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (currentAction == BibaFile.ACCESS_TYPE_WRITE) {
                inputBox.setText("Writer text here for appending...");
                inputBox.setEditable(true);
                return;
            }

            inputBox.setText(all.toString());

            if (currentAction == BibaFile.ACCESS_TYPE_READ) {
                inputBox.setEditable(false);
                return;
            }

            inputBox.setEditable(true);
        });

        saveFile.setOnAction(event -> {
            File file;

            if (currentAction == BibaFile.ACCESS_TYPE_READ) {
                return;
            }

            if ((file = getFileHandler()) == null) {
                return;
            }

            StringBuilder all = new StringBuilder("");
            for (CharSequence cs : inputBox.getParagraphs()) {
                all.append(cs.toString());
            }

            try {
                OutputStream out = new FileOutputStream(file);
                out.write(all.toString().getBytes());
                out.flush();
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addNewUser.setOnAction(event -> {
            diaglogCreateNewUser();
            refreshUserList();
        });

        addNewFile.setOnAction(event -> {
            try {
                diaglogCreateNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            refreshFileList();
        });
    }

    private File getFileHandler() {
        if (curUser == null) {
            warning("LACK PARAMETER","Select User");
            return null;
        }
        if (curFile == null) {
            warning("LACK PARAMETER","Select File");
            return null;
        }

        File rtn = null;
        try {
            rtn = curUser.accessFile(curFile.getPath(), currentAction);
        } catch (RuleConflictException e) {
            warning("CONFLICT WITH RULE: " + e.parseError(), curUser.getName() + "[" + String.valueOf(curUser.getLevel()) +
                    "] try to access \"" + curFile.getPath() + "\" [" + String.valueOf(curFile.getLevel()) + "]");
            return null;
        }

        return rtn;
    }

    private void refreshUserList() {
        ObservableList names = FXCollections.observableArrayList();

        for (String str : bibaFileHandler.getAllUsers()) {
            names.add(str);
        }
        allUsers.setItems(names);
    }

    private void refreshFileList() {
        ObservableList paths = FXCollections.observableArrayList();

        for (String str : bibaFileHandler.getAllFiles()) {
            paths.add(str);
        }

        allFiles.setItems(paths);
    }

    private void warning(String reason, String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText(reason);
        alert.setContentText(info);
        alert.showAndWait();
    }

    private User diaglogCreateNewUser() {
        AtomicReference<String> userName = new AtomicReference<>("");
        final int userLebel;

        TextInputDialog nameDialog = new TextInputDialog("alice");
        nameDialog.setTitle("Creating New User");
        nameDialog.setHeaderText("You are creating a new user");
        nameDialog.setContentText("Please enter new user's name:");

        Optional<String> result1 = nameDialog.showAndWait();

        if (result1.isPresent()){
            List<String> choices = new ArrayList<>();
            choices.add("1");
            choices.add("2");
            choices.add("3");
            choices.add("4");
            ChoiceDialog<String> levelDialog = new ChoiceDialog<>("4", choices);

            levelDialog.setTitle("Creating New User");
            levelDialog.setHeaderText("You are creating a new user");
            levelDialog.setContentText("Please enter new user's level:");

            Optional<String> result2 = levelDialog.showAndWait();

            if (result2.isPresent()) {
                return bibaFileHandler.createNewUser(Integer.parseInt(result2.get()), result1.get());
            }
        }

        return null;
    }

    private BibaFile diaglogCreateNewFile() throws IOException {

        TextInputDialog pathDialog = new TextInputDialog("\\test.text");
        pathDialog.setTitle("Creating New File");
        pathDialog.setHeaderText("You are creating a new file");
        pathDialog.setContentText("Please enter new file's path:");

        Optional<String> result1 = pathDialog.showAndWait();

        if (result1.isPresent()){
            TextInputDialog userDialog = new TextInputDialog();

            userDialog.setTitle("Creating New File");
            userDialog.setHeaderText("You are creating a new file");
            userDialog.setContentText("Please enter file owner:");

            Optional<String> result2 = userDialog.showAndWait();

            if (result2.isPresent()) {
                if (!bibaFileHandler.existUser(result2.get())) {
                    warning("BAD PARAMTER", "NO SUCH USER");
                    return null;
                }
                return bibaFileHandler.createNewFile(result2.get(), result1.get());
            }
        }

        return null;
    }
}
