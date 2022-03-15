package seedu.sherpass.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import seedu.sherpass.exception.InputRepeatedException;
import seedu.sherpass.exception.InvalidInputException;
import seedu.sherpass.task.Task;
import seedu.sherpass.task.TaskList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static seedu.sherpass.constant.Index.DIRECTORY_INDEX;
import static seedu.sherpass.constant.Message.ERROR_CORRUPT_SAVED_FILE_MESSAGE_1;
import static seedu.sherpass.constant.Message.ERROR_CORRUPT_SAVED_FILE_MESSAGE_2;
import static seedu.sherpass.constant.Message.ERROR_CORRUPT_SAVED_FILE_MESSAGE_3;
import static seedu.sherpass.constant.Message.ERROR_IO_FAILURE_MESSAGE;

public class Storage {
    private String saveFilePath;

    /**
     * Creates a constructor for the class Storage.
     * Initialises and creates location for save file if missing.
     *
     * @param filePath Location of the save file.
     * @throws IOException If failed to read save file.
     */
    public Storage(String filePath) throws IOException {
        saveFilePath = filePath;
        System.out.println("Booting up...");
        String[] directoryName = filePath.split("/");
        File saveDirectory = new File(directoryName[DIRECTORY_INDEX]);
        if (saveDirectory.mkdir()) {
            System.out.println("Creating save directory...");
        }
        File saveState = new File(filePath);
        if (saveState.createNewFile()) {
            System.out.println("Creating new save state...");
        }
    }

    // Wipes the existing file
    private void wipeSavedData() {
        try {
            FileWriter fw = new FileWriter(saveFilePath);
            fw.close();
        } catch (IOException e) {
            System.out.println(ERROR_IO_FAILURE_MESSAGE);
            System.exit(1);
        }
    }

    /**
     * Appends new tasks to the save file.
     *
     * @param taskStatus         Mark status of the task.
     * @param newTaskDescription Task Description.
     * @param newTaskByDate      Task Deadline and/or time.
     * @param newTaskDoDate      Task Do date
     */
    public void appendToFile(String taskStatus, String newTaskDescription,
                             String newTaskByDate, String newTaskDoDate) {
        try {
            FileWriter fw = new FileWriter(saveFilePath, true);
            String textToAppend = taskStatus + " | "
                    + newTaskDescription + " | " + newTaskByDate
                    + " | " + newTaskDoDate;

            fw.write(textToAppend + System.lineSeparator());
            fw.close();

        } catch (IOException e) {
            System.out.println(ERROR_IO_FAILURE_MESSAGE);
        }
    }

    /**
     * Returns the JSON representation of the task list.
     *
     * @param taskList The task list to be converted into JSON format.
     * @return JSONObject containing the tasks in the task list.
     */
    private JSONObject convertTaskListToJson(TaskList taskList) {
        JSONObject json = new JSONObject();
        JSONArray tasks = new JSONArray();
        for (Task t : taskList.getTasks()) {
            JSONObject task = new JSONObject();
            task.put("status", t.getStatusIcon());
            task.put("by_date", t.getByDate());
            task.put("do_date", t.getDoOnDate());
            task.put("description", t.getDescription());
            tasks.put(task);
        }
        json.put("tasks", tasks);
        return json;
    }

    /**
     * Overwrites existing saved data in save file with new data.
     *
     * @param taskList Array of tasks that are to be saved.
     */
    public JSONObject writeSaveData(TaskList taskList) {
        JSONObject taskJson = convertTaskListToJson(taskList);
        String taskString = taskJson.toString(4);
        assert taskString != null;
        try {
            FileWriter writer = new FileWriter(saveFilePath);
            writer.write(taskString);
            writer.close();
        } catch (IOException e) {
            System.out.println(ERROR_IO_FAILURE_MESSAGE);
        }
        return taskJson;
    }

    private boolean isTaskRepeated(ArrayList<Task> saveTaskList, int index) {
        for (int j = index + 1; j < saveTaskList.size(); j++) {
            if (saveTaskList.get(index).getDescription().trim()
                    .equalsIgnoreCase(saveTaskList.get(j).getDescription().trim())) {
                return true;
            }
        }
        return false;
    }

    private void checkForRepeatedInputs(ArrayList<Task> saveTaskList) throws InputRepeatedException {
        for (int i = 0; i < saveTaskList.size() - 1; i++) {
            if (isTaskRepeated(saveTaskList, i)) {
                throw new InputRepeatedException();
            }
        }
    }

    /**
     * Loads back the save file onto the program.
     *
     * @return ArrayList containing the tasks saved in the data file
     * @throws IOException           If an I/O error occurs while reading the data file
     * @throws InvalidInputException If the data has missing fields for a task
     * @throws JSONException         If the data file has an invalid JSON format
     */
    public ArrayList<Task> load() throws IOException, InvalidInputException, JSONException {
        ArrayList<Task> taskList = new ArrayList<>();
        List<String> dataLines = Files.readAllLines(new File(saveFilePath).toPath());
        if (dataLines.size() > 0) {
            String dataString = String.join("", dataLines);
            JSONObject dataJson = new JSONObject(dataString);
            JSONArray array = dataJson.getJSONArray("tasks");

            for (int i = 0; i < array.length(); i++) {
                JSONObject taskData = array.getJSONObject(i);
                taskList.add(Parser.parseSavedData(taskData));
            }
        }
        return taskList;
    }

    /**
     * Creates a new save file or exits the program.
     *<p>
     * When the save file fails to load, the user decides if the program creates new save file
     * or the user can manually inspect the save file.
     *</p>
     * @param ui Ui for printing messages
     */
    public void handleCorruptedSave(Ui ui) {
        ui.showToUser(ERROR_CORRUPT_SAVED_FILE_MESSAGE_1);
        String response = "";

        while (!response.equalsIgnoreCase("y") && !response.equalsIgnoreCase("n")) {
            ui.showToUser(ERROR_CORRUPT_SAVED_FILE_MESSAGE_2);
            response = ui.readCommand();
        }

        assert response.equalsIgnoreCase("y") || response.equalsIgnoreCase("n");

        if (response.equalsIgnoreCase("y")) {
            wipeSavedData();
        } else {
            ui.showToUser(ERROR_CORRUPT_SAVED_FILE_MESSAGE_3);
            System.exit(1);
        }
    }

}
