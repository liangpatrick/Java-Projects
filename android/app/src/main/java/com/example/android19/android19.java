package com.example.android19;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.android19.databinding.AlbumListBinding;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class android19 extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    final Context context = this;
    private AlbumListBinding binding;
    ListView observableList;
    ArrayAdapter<User.Album> adapter;
    String response;
    public static List<User.Album> list = new ArrayList<User.Album>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        User.Album a = new User.Album("Chungus");
//        User.Album b = new User.Album("Casdf");
//        User.Album c = new User.Album("asdd");
//        list.add(a);
//        list.add(b);
//        list.add(c);
        list = loadAlbums(context);
        if (list == null) {
            list = new ArrayList<User.Album>();
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.album_list);
        TextView toolbar = (TextView) findViewById(R.id.title);
        toolbar.setText("Albums");

        observableList = (ListView) findViewById(R.id.photo_list);
        observableList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        observableList.setSelection(0);
        adapter = new ArrayAdapter<User.Album>(this, R.layout.album, list);
        observableList.setAdapter(adapter);

        observableList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            FloatingActionButton eButton = (FloatingActionButton) findViewById(R.id.editBtn);
            FloatingActionButton dButton = (FloatingActionButton) findViewById(R.id.deleteBtn);
            FloatingActionButton sButton = (FloatingActionButton) findViewById(R.id.searchBtn);

            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int tempPos = position;
                observableList.requestFocusFromTouch();
                observableList.setSelection(position);
                eButton.setVisibility(View.VISIBLE);
                dButton.setVisibility(View.VISIBLE);
                sButton.setVisibility(View.INVISIBLE);

                eButton.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View myView) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Edit album name " + adapter.getItem(tempPos).getAlbumName());

                        final EditText editText = new EditText(context);
                        editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(editText);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (searchAlbums(editText.getText().toString().toLowerCase()) != -1) {
                                    AlertDialog.Builder temp = new AlertDialog.Builder(context);
                                    temp.setTitle("Duplicate");
                                    temp.setMessage("This album already exists.");
                                    temp.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            return;
                                        }
                                    });
                                    temp.show();
                                } else {
                                    response = editText.getText().toString();
                                    list.get(tempPos).setAlbumName(response);
//                                    list.add(temp);
                                    saveAlbums(context);
                                    observableList.setAdapter(adapter);
                                    eButton.setVisibility(View.INVISIBLE);
                                    dButton.setVisibility(View.INVISIBLE);
                                    sButton.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                eButton.setVisibility(View.INVISIBLE);
                                dButton.setVisibility(View.INVISIBLE);
                                sButton.setVisibility(View.VISIBLE);
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
                dButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View myView) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Are you sure you want to delete album " + adapter.getItem(tempPos).getAlbumName() + "?");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(tempPos);
                                saveAlbums(context);
                                observableList.setAdapter(adapter);
                                eButton.setVisibility(View.INVISIBLE);
                                dButton.setVisibility(View.INVISIBLE);
                                sButton.setVisibility(View.VISIBLE);
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                eButton.setVisibility(View.INVISIBLE);
                                dButton.setVisibility(View.INVISIBLE);
                                sButton.setVisibility(View.VISIBLE);
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                });
                return true;
            }
        });

        observableList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> p, View v, int x, long y) {
                Intent i = new Intent(android19.this, Open_Album_Activity.class);
                i.putExtra("index", x);
                startActivity(i);
            }
        });

        FloatingActionButton sButton = (FloatingActionButton) findViewById(R.id.searchBtn);
        sButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View myView) { //TAG SEARCH ... COME BACK TO THIS
                final Dialog d = new Dialog(context);
                d.setTitle("Search by tags.");
                d.setContentView(R.layout.search_photos);

                final AutoCompleteTextView myText = (AutoCompleteTextView) d.findViewById(R.id.tag_info);
                Spinner getType = (Spinner) d.findViewById(R.id.dialog_spinner);
                Button cancelSearch = (Button) d.findViewById(R.id.dialogCancel);
                Button enter = (Button) d.findViewById(R.id.dialogSearch);

                final ArrayList<String> pTags = new ArrayList<String>();
                final ArrayList<String> lTags = new ArrayList<String>();

                for (int x = 0; x < list.size(); x++)
                    for (int y = 0; y < list.get(x).getPhotos().size(); y++) {
                        if (list.get(x).getPhotos().get(y).personTags() == null)
                            continue;
                        pTags.addAll(list.get(x).getPhotos().get(y).personTags());

                    }
                for (int x = 0; x < list.size(); x++)
                    for (int y = 0; y < list.get(x).getPhotos().size(); y++) {
                        if (list.get(x).getPhotos().get(y).locationTags() == null)
                            continue;
                        pTags.addAll(list.get(x).getPhotos().get(y).locationTags());

                    }
                if (getType.getSelectedItem().toString().trim().equals("person")) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, pTags);
                    myText.setAdapter(adapter);

                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, lTags);
                    myText.setAdapter(adapter);
                }

                enter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View myView) {
                        User.Album.searchResults.photoList.clear();
                        EditText editText = (EditText) d.findViewById(R.id.tag_info);
                        Spinner spinner = (Spinner) d.findViewById(R.id.dialog_spinner);
                        if (editText.getText().toString().trim().isEmpty()) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Invalid");
                            alert.setMessage("Must input at least one character");
                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    return;
                                }
                            });
                            alert.show();
                        } else {
                            String type = spinner.getSelectedItem().toString();
                            String value = editText.getText().toString().toLowerCase();
                            System.out.println(value);

                            if (type.equals("location")) {

                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, lTags);
                                myText.setAdapter(adapter);
                                for (int x = 0; x < list.size(); x++) {
                                    for (int y = 0; y < list.get(x).getPhotos().size(); y++) {
                                        ArrayList<String> location = list.get(x).getPhotos().get(y).locationTags();
                                        System.out.println(location.get(x));
                                        if (location == null) break;
                                        if (location.isEmpty()) break;
                                        if (location.contains(value)) {
                                            User.Album.searchResults.addPhoto(list.get(x).getPhotos().get(y));
                                        } else {
                                            for (int z = 0; z < location.size(); z++) {
                                                if (location.get(z).startsWith(value)) {
                                                    User.Album.searchResults.addPhoto(list.get(x).getPhotos().get(y));
                                                    break;
                                                }
                                            }

                                        }

                                    }
                                }
                            } else {
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, pTags);
                                myText.setAdapter(adapter);
                                for (int x = 0; x < list.size(); x++) {
                                    for (int y = 0; y < list.get(x).getPhotos().size(); y++) {
                                        ArrayList<String> person = list.get(x).getPhotos().get(y).personTags();
                                        if (person == null) break;
                                        if (person.isEmpty()) break;
                                        String[] PERSON = new String[person.size()];
                                        PERSON = person.toArray(PERSON);
                                        if (person.contains(value)) {
                                            User.Album.searchResults.addPhoto(list.get(x).getPhotos().get(y));
                                        } else {
                                            for (int z = 0; z < person.size(); z++) {
                                                if (person.get(z).startsWith(value)) {
                                                    User.Album.searchResults.addPhoto(list.get(x).getPhotos().get(y));
                                                    break;
                                                }
                                            }

                                        }

                                    }
                                }
                            }

                        }
                        if(Integer.parseInt(User.Album.searchResults.getNumOfPhotos()) == 0){
                            AlertDialog.Builder error = new AlertDialog.Builder(context);
                            error.setTitle("Search empty");
                            error.setMessage("No matches found");
                            error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    return;
                                }
                            });
                            error.show();
                        }
                        else {
                            Intent i = new Intent(android19.this, MainView.class);
                            i.putExtra("index", 0);
                            startActivity(i);
                        }
                    }
                });
                cancelSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View myView) {
                        d.dismiss();
                    }
                });
                d.show();
            }


        });


        FloatingActionButton addAlbum = (FloatingActionButton) findViewById(R.id.addBtn);
        addAlbum.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View myView) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Enter a new Album name.");
                FloatingActionButton deleteBtn = (FloatingActionButton) findViewById(R.id.deleteBtn);
                FloatingActionButton editBtn = (FloatingActionButton) findViewById(R.id.editBtn);
                deleteBtn.setVisibility(myView.INVISIBLE);
                editBtn.setVisibility(myView.INVISIBLE);

                final EditText userText = new EditText(context);
                userText.setInputType(InputType.TYPE_CLASS_TEXT);
                alert.setView(userText);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int x) {
                        if (userText.getText().toString().trim().isEmpty()) {
                            AlertDialog.Builder error = new AlertDialog.Builder(context);
                            error.setMessage("Field is empty.");
                            error.setTitle("Input Error");
                            error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int x) {
                                    d.dismiss();
                                    return;
                                }
                            });
                            error.show();
                        } else if (searchAlbums(userText.getText().toString().toLowerCase()) != -1) {
                            AlertDialog.Builder error = new AlertDialog.Builder(context);
                            error.setMessage("This album already exists.");
                            error.setTitle("Input Error");
                            error.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface d, int x) {
                                    d.dismiss();
                                    return;
                                }
                            });
                            error.show();
                        } else {
                            response = userText.getText().toString();
                            User.Album temp = new User.Album(response);
                            list.add(temp);
                            saveAlbums(context);
                            adapter = new ArrayAdapter<User.Album>(context, R.layout.album, list);
                            observableList.setAdapter(adapter);
                        }
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface d, int x) {
                        d.cancel();
                    }
                });
                alert.show();
            }
        });
    }

    public int searchAlbums(String name) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAlbumName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    public static List<User.Album> loadAlbums(Context context) {
        List<User.Album> temp = null;
        try {
            FileInputStream in = context.openFileInput("albums.ser");
            ObjectInputStream oIn = new ObjectInputStream(in);
            temp = (List<User.Album>) oIn.readObject();

            if (temp == null) {
                temp = new ArrayList<User.Album>();
            }
            oIn.close();
            in.close();
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
        return temp;
    }

    public static void saveAlbums(Context context) {
        ObjectOutputStream out;
        try {
            FileOutputStream write = context.openFileOutput("albums.ser", Context.MODE_PRIVATE);
            ObjectOutputStream object = new ObjectOutputStream(write);
            object.writeObject(list);
            write.close();
            object.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}