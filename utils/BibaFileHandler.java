package utils;

import java.io.*;
import java.util.*;

public class BibaFileHandler implements Serializable{
    static String FILE_PATH_ROOT = ".\\root";

    private static BibaFileHandler bibaFileHandler = null;

    private static File bibaFileSystemUserList = null;
    private static File bibaFileSystemFileList = null;

    // <User's name, User object>
    private static Map<String, User> bibaUserList = null;
    // <File's path, BibaFile object>
    private static Map<String, BibaFile> bibaFileList = null;

    private BibaFileHandler() throws IOException {

        File BibaFileSystemRoot = new File(FILE_PATH_ROOT);
        if (!BibaFileSystemRoot.exists()) {
            BibaFileSystemRoot.mkdir();
        }

        bibaFileSystemFileList = new File(FILE_PATH_ROOT + "\\.files");
        if (!bibaFileSystemFileList.exists()) {
            bibaFileSystemFileList.createNewFile();
        } else {
            bibaFileList = (Map<String, BibaFile>) syncFromFile(bibaFileSystemFileList);
            bibaFileList = (bibaFileList == null) ? new HashMap<>(): bibaFileList;
        }

        bibaFileSystemUserList = new File(FILE_PATH_ROOT + "\\.users");
        if (!bibaFileSystemUserList.exists()) {
            bibaFileSystemUserList.createNewFile();
        } else {
            bibaUserList = (Map<String, User>) syncFromFile(bibaFileSystemUserList);
            bibaUserList = (bibaUserList == null) ? new HashMap<>(): bibaUserList;
        }

//        System.out.println("Users");
//        System.out.println(bibaUserList.size());
//        System.out.println("Files");
//        System.out.println(bibaFileList.size());
    }

    public static BibaFileHandler getInstance() {
        if (bibaFileHandler == null) {
            try {
                bibaFileHandler = new BibaFileHandler();
            } catch (IOException e) {
                System.out.println("Biba file handler failed!");
                e.printStackTrace();
            }
        }
        return bibaFileHandler;
    }

    private Object syncFromFile(File file){
        assert file != null;
        Object object = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
            object =  objectInputStream.readObject();
            objectInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        return object;
    }

    private void flushToFile(File file, Object object) {
        assert file != null;
        try{
            ObjectOutputStream objectOutputStreams = new ObjectOutputStream(new FileOutputStream(file));
            objectOutputStreams.writeObject(object);
            objectOutputStreams.flush();
            objectOutputStreams.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanUp() {
        flushToFile(bibaFileSystemUserList, bibaUserList);
        flushToFile(bibaFileSystemFileList, bibaFileList);
    }

    public boolean existUser(String name) {
        return bibaUserList.containsKey(name);
    }

    public User getExistUser(String name) {
        return bibaUserList.get(name);
    }

    public int getUsersTotal() {
        return bibaUserList.size();
    }

    public String[] getAllUsers() {
        String[] rtn = new String[bibaUserList.size()];
        bibaUserList.keySet().toArray(rtn);
        return rtn;
    }

    public boolean existFile(String path) {
        return bibaFileList.containsKey(path);
    }

    public BibaFile getExistFile(String path) {
        return bibaFileList.get(path);
    }

    public int getFilesTotal() {
        return bibaFileList.size();
    }

    public String[] getAllFiles() {
        String[] rtn = new String[bibaFileList.size()];
        bibaFileList.keySet().toArray(rtn);
        return rtn;
    }

    public User createNewUser(int level, String name){
        if (this.existUser(name)) {
            return this.getExistUser(name).changeLevel(level);
        }
        User user = new User(level, name);
        bibaUserList.put(name, user);
        return user;
    }

    public BibaFile createNewFile(String name, String path) throws IOException {
        User user;
        BibaFile file;

        if (!this.existUser(name)) {
            return null;
        }
        user = this.getExistUser(name);

        if (this.existFile(path)) {
            return this.getExistFile(path);
        }

        file = new BibaFile(path, user.getLevel());
        File rtn = new File(FILE_PATH_ROOT + path);
        if (!rtn.exists()) {
            rtn.getParentFile().mkdirs();
            rtn.createNewFile();
        }
        bibaFileList.put(path, file);
        return file;
    }

    public void revokeUser(String name) {
        bibaUserList.remove(name);
    }

    public File accessBibaFile(String name, String path, int type) throws RuleConflictException {
        User user;
        BibaFile file;
        File rtn;

        if (!this.existFile(path) || !this.existUser(name)) {
            return null;
        }

        user = getExistUser(name);
        file = getExistFile(path);

        rtn = new File(FILE_PATH_ROOT + path);
        if (!rtn.exists()) {
            return null;
        }
        rtn.setExecutable(false);

        switch (type) {
            case BibaFile.ACCESS_TYPE_READ:
                if (user.getLevel() < file.getLevel()) {
                    throw new RuleConflictException(RuleConflictException.READ_DOWN);
                }
                rtn.setReadOnly();
                return rtn;

            case BibaFile.ACCESS_TYPE_WRITE:
                if (user.getLevel() > file.getLevel()) {
                    throw new RuleConflictException(RuleConflictException.WRITE_UP);
                }
                rtn.setReadable(false);
                rtn.setWritable(true);
                return rtn;

            case BibaFile.ACCESS_TYPE_READ_WRITE:
                if (user.getLevel() != file.getLevel()) {
                    throw new RuleConflictException(RuleConflictException.LEVEL_NOT_EQUAL);
                }
                rtn.setReadable(true);
                rtn.setWritable(true);
                return rtn;

            default:
                return null;
        }
    }
}