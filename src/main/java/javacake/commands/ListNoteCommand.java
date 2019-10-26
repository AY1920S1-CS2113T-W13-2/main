package javacake.commands;

import javacake.Logic;
import javacake.exceptions.DukeException;
import javacake.notes.Note;
import javacake.notes.NoteList;
import javacake.storage.Profile;
import javacake.storage.Storage;
import javacake.ui.Ui;

import java.util.ArrayList;

public class ListNoteCommand extends Command {

    public ArrayList<Note> notesArchive;

    /**
     * Prints out the names of all the notes.
     * Notes can be stored beforehand or created when program is running using CreateNoteCommand.
     * @param logic tracks current location in program.
     * @param ui the Ui responsible for outputting messages.
     * @param storage Storage needed to write the updated data.
     * @param profile Profile of the user.
     * @return String of the file names of the notes.
     * @throws DukeException If file does not exist.
     */
    public String execute(Logic logic, Ui ui, Storage storage, Profile profile) throws DukeException {
        StringBuilder sb = new StringBuilder();
        notesArchive = new NoteList().compileNotes();
        sb.append("You have "+ notesArchive.size() + " note(s) available!").append("\n");
        int index = 1;
        for (Note n : notesArchive) {
            sb.append(Integer.toString(index) + ". ");
            sb.append(n.getName()).append("\n");
            index++;
        }
        return sb.toString();
    }
}
