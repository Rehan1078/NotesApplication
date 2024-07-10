package com.example.notesapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;


public class AddNotesFragment extends Fragment {
    private EditText titleEditText, contentEditText;
    private ImageButton saveNoteBtn;
    private TextView pageTitleTextView, deleteNoteTextViewBtn;
    private String title, content, docId;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_notes, container, false);

        titleEditText = view.findViewById(R.id.notes_title_text);
        contentEditText = view.findViewById(R.id.notes_content_text);
        saveNoteBtn = view.findViewById(R.id.save_note_btn);
        pageTitleTextView = view.findViewById(R.id.page_title);

        // Receive data
        if (getArguments() != null) {
            title = getArguments().getString("title");
            content = getArguments().getString("content");
            docId = getArguments().getString("docId");
        }

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode) {
            pageTitleTextView.setText("Edit your note");
            deleteNoteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener(v -> saveNote());


        return view;
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();
        if (TextUtils.isEmpty(noteTitle)) {
            titleEditText.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;
        if (isEditMode) {
            // Update the note
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            // Create new note
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Note is added
                    Utility.showToast(getActivity(), "Note added successfully");
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Utility.showToast(getActivity(), "Failed while adding note");
                }
            }
        });
    }



}