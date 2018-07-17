package vahren.fr.loot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import fr.vahren.loot.Item;
import fr.vahren.loot.LootGen;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener{

    public LootGen lootGen;
    public TextView unique;
    public TextView name;
    public TextView stats;
    public TextView powers;
    public TextView tags;
    public TextView price;
    public Item item;

    NumberFormat formatter = new DecimalFormat("#0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // get textViews
        unique = findViewById(R.id.unique);
        name = findViewById(R.id.name);
        stats = findViewById(R.id.stats);
        powers = findViewById(R.id.powers);
        tags = findViewById(R.id.tags);
        price = findViewById(R.id.price);

        try {
            lootGen = new LootGen(readResource(R.raw.gear),readResource(R.raw.nouns),readResource(R.raw.adjectives),
                    readResource(R.raw.qualif),readResource(R.raw.modifier),readResource(R.raw.material), readResource(R.raw.names));
            lootGen.defaultTree();
            // touch
            findViewById(R.id.container).setOnTouchListener(this);
            changeItem();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<String> readResource(int resId) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getApplicationContext().getResources().openRawResource(resId)));
        List<String> s = new LinkedList<>();
        String readLine;
        while ((readLine = reader.readLine()) != null) {
            s.add(readLine);
        }
        reader.close();
        return s;
    }

    private String clean(String s) {
        return s.replaceAll("'\\s+","'").replaceAll("\\s+"," ");
    }

    private void changeItem() {
        // new item
        item = lootGen.gen();

        if (item.uniqueName != null) {
            unique.setVisibility(View.VISIBLE);
            unique.setText(clean(item.uniqueName));
        } else {
            unique.setVisibility(View.INVISIBLE);
        }
        name.setText(clean(item.name));
        if (!item.stats.isEmpty()) {
            List<String> stats = new LinkedList<>();
            for(String s: item.stats.keySet()){
                final String statString =
                        item.stats.get(s) > 0 ? "+" + item.stats.get(s) : item.stats.get(s).toString();
                if (s.endsWith("%")) {
                    stats.add( s.substring(0, s.length() - 1) + " = " + statString + "%");
                } else {
                    stats.add(s + " = " + statString);
                }
            }
            this.stats.setText(join("\n", stats));
        } else {
            stats.setText("");
        }
        if (!item.powers.isEmpty()) {
            this.powers.setText(join("\n", item.powers));
        } else {
            this.powers.setText("");
        }
        if (!item.tags.isEmpty()) {
            this.tags.setText("[ " +join(" ", item.tags) + " ]");
        } else {
            this.tags.setText("");
        }
        price.setText(formatter.format(Math.max(0,item.price.compute(0))) + " po");
    }

    private String color(String stat){
        switch (stat.replaceAll("%","")) {
            case "SAGESSE": return "cyan";
            case "BLOQUAGE": return "grey";
            case "INTELLIGENCE": return "purple";
            case "DEXTERITE": return "green";
            case "FORCE": return "brown";
            case "PERCEPTION": return "lime";
            case "DEFENSE": return "grey";
            case "VITESSE": return "green";
            case "PRIX": return "yellow";
            case "CHARISME": return "pink";
            case "DEGAT": return "red";
            case "POISON": return "green";
            case "DEGAT_EAU":
            case "RESISTANCE_EAU": return "blue";
            case "DEGAT_FEU":
            case "RESISTANCE_FEU": return "red";
            case "DEGAT_TERRE":
            case "RESISTANCE_TERRE": return "green";
            case "DEGAT_AIR":
            case "RESISTANCE_AIR": return "cyan";
            case "DEGAT_DIVIN":
            case "RESISTANCE_DIVIN": return "light_blue";
            case "DEGAT_TENEBRE":
            case "RESISTANCE_TENEBRE": return "black";
            case "DEGAT_FOUDRE":
            case "RESISTANCE_FOUDRE": return "yellow";
            case "PORTEE": return "";
            default:
                return "black";
        }
    }

    private String join(String delimiter, List<String> strings) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for(String s: strings) {
            sb.append(s);
            if (i < strings.size()-1){
                sb.append(delimiter);
            }
            i++;
        }
        return sb.toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        changeItem();
        return false;
    }
}
