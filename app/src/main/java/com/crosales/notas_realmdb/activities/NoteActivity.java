package com.crosales.notas_realmdb.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.crosales.seccion7_realmdb.R;
import com.crosales.notas_realmdb.adapters.NoteAdapter;
import com.crosales.notas_realmdb.models.Board;
import com.crosales.notas_realmdb.models.Notas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;

public class NoteActivity extends AppCompatActivity implements RealmChangeListener<Board> {

    private ListView listView;
    private FloatingActionButton fab;

    private NoteAdapter adapter;
    private RealmList<Notas> notas;
    private Realm realm;

    private int boardId;
    private Board board;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        //DB Realm
        realm = Realm.getDefaultInstance();

        //se recupera el id que llega
        if (getIntent().getExtras() != null)
            boardId = getIntent().getExtras().getInt("id");

        //con el id de arriba recuperamos el Board y de Board obtenemos las notas
        board = realm.where(Board.class).equalTo("id", boardId).findFirst();
        board.addChangeListener(this);
        notas = board.getNotas();

        this.setTitle(board.getTitle());

        //instancias de UI
        fab = (FloatingActionButton) findViewById(R.id.fabAddNote);
        listView = (ListView) findViewById(R.id.listViewNote);
        adapter = new NoteAdapter(this, notas, R.layout.list_view_note_item);

        listView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertForCreatingNote("Agregar Nueva Nota", "Escribe una nota para " + board.getTitle() + ".");
            }
        });

    }


    //** CRUD Actions **/
    private void crearNuevaNota(String note){
        realm.beginTransaction();
        Notas _note = new Notas(note);
        realm.copyToRealm(_note);
        board.getNotas().add(_note);
        realm.commitTransaction();

    }

    private void editNota(String nuevaNotaDescripcion, Notas nota){
        realm.beginTransaction();
        nota.setDescription(nuevaNotaDescripcion);
        realm.copyToRealmOrUpdate(nota);
        realm.commitTransaction();
    }

    private void deleteNota(Notas nota){
        realm.beginTransaction();
        board.deleteFromRealm();
        realm.commitTransaction();
    }

    private void deleteAll(){
        realm.beginTransaction();
        board.getNotas().deleteAllFromRealm();
        realm.commitTransaction();
    }




    //** Dialogs **/
    private void showAlertForCreatingNote(String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);


        builder.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String note = input.getText().toString().trim();
                if (note.length() > 0)
                    crearNuevaNota(note);
                else
                    Toast.makeText(getApplicationContext(), "La nota no puede estar vacia", Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showAlertForEditingNote(String title, String message, final Notas nota) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null);
        builder.setView(viewInflated);

        final EditText input = (EditText) viewInflated.findViewById(R.id.editTextNewNote);
        //pasa el titulo seleccionado al cuadro de alerta
        input.setText(nota.getDescription());


        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String notaDescripcion = input.getText().toString().trim();
                if (notaDescripcion.length() == 0)
                    Toast.makeText(getApplicationContext(), "Se requiere texto para la nota para ser editada", Toast.LENGTH_LONG).show();
                else if (notaDescripcion.equals(nota.getDescription()))
                    Toast.makeText(getApplicationContext(), "La nota es la misma que estaba antes", Toast.LENGTH_LONG).show();
                else
                    editNota(notaDescripcion, nota);

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /* Events*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //EDITAR
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_nota_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()){
            case R.id.delete_nota:
                deleteNota(notas.get(info.position));
                return true;
            case R.id.edit_nota:
                showAlertForEditingNote("Editar Tablero", "Cambia el nombre del tablero", notas.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }


    }
    //Fin EDITAR


    @Override
    public void onChange(Board board) {
        adapter.notifyDataSetChanged();
    }
}
