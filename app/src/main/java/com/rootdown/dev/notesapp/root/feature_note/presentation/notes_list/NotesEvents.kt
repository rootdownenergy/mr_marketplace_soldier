package com.rootdown.dev.notesapp.root.feature_note.presentation.notes_list

import com.rootdown.dev.notesapp.root.feature_note.domain.model.Note
import com.rootdown.dev.notesapp.root.feature_note.domain.util.NoteOrder

sealed class NotesEvents {
    data class Order(val noteOrder: NoteOrder): NotesEvents()
    data class DeleteNote(val note: Note): NotesEvents()
    object RestoreNote: NotesEvents()
    object ToggleOrderSection: NotesEvents()
}
