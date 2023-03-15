package com.sintel.webcontentanalyzer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TintInfo;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.math.RoundingMode;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class AnalyseActivity extends AppCompatActivity {
    String titre,texte,text_clean;
    TextView textView_titre,nb_mot,nb_phrase,nb_caractere,nb_space,long_mot,longue_phrase;
    TableLayout tableLayout_mots, tableLayout_longMot;
    ArrayList<Mot> motArrayList,longMotList;
    RecyclerView recyclerView, recyclerView_longMot;
    String[] tabMots, tab_phrases;
    public static  String [] stop_words = {"'à' ,'demi' ,'peine' ,'peu' ,'près' ,'absolument' ,'actuellement' ,'ainsi' ,'alors'' ,'apparemment' ,'approximativement' ,'après' ,'demain' ,'assez' ,'assurément' ,'au' ,'aucun' ,'aucunement' ,'aucuns' ,'aujourd'hui' ,'auparavant' ,'aussi' ,'aussitôt' ,'autant' ,'autre' ,'autrefois','autrement','avant','avec','avoir','beaucoup','bien','bientôt','bon','c','ça','car','carrément','ce','cela','cependant','certainement','certes','ces','ceux','chaque','ci','comme','comment','complètement','d','abord','dans','davantage','de','début','dedans','dehors','déjà','demain','depuis','derechef','des','désormais','deux','devrait','diablement','divinement','doit','donc','dorénavant','dos','droite','drôlement','du','elle','elles','en','vérité','encore','enfin','ensuite','entièrement','entre','temps','environ','essai','est','et','étaient','état','été','étions','être','eu','extrêmement','fait','faites','fois','font','force','grandement','guère','habituellement','haut','hier','hors','ici','il','ils','infiniment','insuffisamment','jadis','jamais','je','joliment','ka','la','là','le','les','leur','leurs','lol','longtemps','lors','ma','maintenant','mais','mdr','même','mes','moins','mon','mot','naguère','ne','ni','nommés','non','notre','nous','nouveaux','nullement','ou','où','oui','par','parce','parfois','parole','pas','mal','passablement','personne','personnes','peu','peut','être','pièce','plupart','plus','plutôt','point','pour','pourquoi','précisément','premièrement','presque','probablement','prou','puis','quand','quasi','quasiment','que','quel','quelle','quelles','quelque','quelquefois','quels','qui','quoi','quotidiennement','rien','rudement','s','sa','sans','doute','ses','seulement','si','sien','sitôt','soit','son','sont','soudain','sous','souvent','soyez','subitement','suffisamment','sur','t','ta','tandis','tant','tantôt','tard','tellement','tels','terriblement','tes','ton','tôt','totalement','toujours','tous','tout','toutefois','très','trop','tu','un','une','valeur','vers','voie','voient','volontiers','vont','votre','vous','vraiment','vraisemblablement'\n"};
    public static String [] Mots_vides ;
    public static ArrayList<String> wordList = new ArrayList<String>();
    public static ArrayList<String> Liste_Mots = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);

        titre=(String) getIntent().getSerializableExtra("titre");
        texte=(String) getIntent().getSerializableExtra("texte");
        text_clean=Clean_texte(texte);

        recyclerView =findViewById(R.id.liste_mots);
        //recyclerView_longMot=findViewById(R.id.longueur_mots);
        nb_mot=findViewById(R.id.nbMots);
        nb_phrase=findViewById(R.id.nbPhrases);
        nb_mot=findViewById(R.id.nbMots);
        nb_caractere=findViewById(R.id.nbCar);
        nb_space=findViewById(R.id.nbSpace);
        long_mot=findViewById(R.id.LongMot);
        longue_phrase=findViewById(R.id.LonguePhrase);

        textView_titre=findViewById(R.id.titre_page);
        textView_titre.setText(titre);
        Afficher_Mots();
        //AfficherLongMot();

        //Affichage de nombre des mots
        tabMots=texte.trim().split("\\s|\n|\\t|,|;|-|_|\\.|\\?|!|:|@|\\{|\\}|\\{|\\[|\\]|\\(|\\)|\\*|/|\\+|\\&");
        nb_mot.setText(""+tabMots.length);

        //Affichage de nombre des phrases
        tab_phrases=texte.trim().split("\\.|\n|\\?|!");
        nb_phrase.setText(""+tab_phrases.length);

        //Affichage du plus long mot et de la plus longue phrase
        //tabMots=texte.trim().split("\\s+");
        long_mot.setText(""+RechercheLongMot(text_clean));
        longue_phrase.setText(RechercheLonguePhrase(texte));

        //nbre d'espace
        nb_space.setText(""+Space(texte));
        nb_caractere.setText(""+CompterCaractere(texte));
    }

    public int CompterCaractere(String texte) {
        int nbchar=0;
        for (char c:texte.toCharArray()){
            if (c!=' ') {
                nbchar++;
            }
        }
        return nbchar;
    }

    public int Space(String texte){
        int count=0;
        for (int i=0;i< texte.length();i++){
            if (Character.isWhitespace(texte.charAt(i))) count++;
        }
        return count;
    }

    private String RechercheLonguePhrase(String phrases) {
        tab_phrases=phrases.trim().split("\\.|\n|\\?|!");
        String longuePhrase = tab_phrases[0];

        for (int i =1; i<tab_phrases.length; i++){

            if(tab_phrases[i].length()>longuePhrase.length()){
                longuePhrase=tab_phrases[i];
            }
        }

        return longuePhrase;
    }

    private String RechercheLongMot(String mots) {
        tabMots=mots.trim().split(" ");
        String longMot = tabMots[0];
        String word = "";
        for (int i =0; i<tabMots.length; i++){
            if(tabMots[i].length()>longMot.length()){
                longMot=tabMots[i];
            }
        }
        return longMot;
    }

    private void Afficher_Mots() {
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Mot_Adapter adapter = new Mot_Adapter(this,TextTokenizer.getKeywordCounts(text_clean.trim().split(" ")));
            recyclerView.setAdapter(adapter);
        //Toast.makeText(this, ""+TextTokenizer.getKeywordCounts(texte.toLowerCase().split("\\s|\n|\\t|,|;|-|'|_|\\.|\\?|!|:|@|\\{|\\}|\\{|\\[|\\]|\\(|\\)|\\*|/|\\+|\\&")), Toast.LENGTH_LONG).show();
    }

    private ArrayList<Mot> getList() {
        tabMots=texte.trim().split("\\s");
        Hashtable<String,Integer> tableMots = new Hashtable<>();
        StringTokenizer st;
        String Mot;
        int nbOcc;
        while (texte!=null){
            st = new StringTokenizer(texte, " ,.;:_-+*/\\.;\n\"'{}()=><\t!?");
            while(st.hasMoreTokens())
            {
                Mot = st.nextToken();
                if (tableMots.containsKey(Mot))
                {
                    nbOcc = ((Integer)tableMots.get(Mot)).intValue();
                    nbOcc++;
                }
                else nbOcc = 1;
                tableMots.put(Mot, new Integer(nbOcc));
            }
            Enumeration lesMots = tableMots.keys();
            while (lesMots.hasMoreElements())

            {
                Mot = (String)lesMots.nextElement();
                nbOcc = ((Integer)tableMots.get(Mot)).intValue();

                if (nbOcc>=1){
                    motArrayList=new ArrayList<Mot>();
                    Mot mot = new Mot();

                    mot.setMot(Mot);
                    mot.setOccurence(nbOcc);
                    mot.setFrequence(nbOcc/tabMots.length);
                    motArrayList.add(mot);
                }
            }
        }
        return motArrayList;
    }

    private void AfficherLongMot() {
        recyclerView_longMot.setHasFixedSize(true);
        recyclerView_longMot.setLayoutManager(new LinearLayoutManager(this));
        MotAdapter adapter = new MotAdapter(this,getLongMotList());
        recyclerView_longMot.setAdapter(adapter);
    }

    private ArrayList<Mot>ObtenirListeMots(){
        tabMots=texte.trim().split("\\s|\n|\\t|,|;|-|_|\\.|\\?|!|:|@|\\{|\\}|\\{|\\[|\\]|\\(|\\)|\\*|/|\\+|\\&");
        motArrayList=new ArrayList<Mot>();
        Mot mot = new Mot();
        int occurence_mot=0;

        for (String word : tabMots){
            Liste_Mots.add(word);
        }

        // count occurence of words
        for (int i=0; i<Liste_Mots.size();i++){
            for (int j=0; j< tabMots.length; j++)
            {
                if(Liste_Mots.get(i).contains(tabMots[j])){
                    occurence_mot++;
                }
            }
            mot.setMot(Liste_Mots.get(i));
            mot.setOccurence(occurence_mot);
            mot.setFrequence((occurence_mot/tabMots.length));
            motArrayList.add(mot);
        }
        return motArrayList;
    }

    private ArrayList<Mot> getLongMotList() {
        tabMots=texte.trim().split("\\s|\n|\\t|,|;|-|_|\\.|\\?|!|:|@|\\{|\\}|\\{|\\[|\\]|\\(|\\)|\\*|/|\\+|\\&");
        motArrayList=new ArrayList<Mot>();
        Mot mot = new Mot();
        int occurence_mot=1,i=0,j;

        for (i=0; i < tabMots.length; i++)
        {
            for (j=1; j<tabMots.length; j++){
                   if(tabMots[j].contains(tabMots[i]))
                   {
                    occurence_mot++;
                   }
            }
            mot.setMot(tabMots[i]);
            mot.setOccurence(occurence_mot);
            mot.setFrequence((occurence_mot/tabMots.length));
            motArrayList.add(mot);
        }
        return motArrayList;
    }

    public static class MotAdapter extends RecyclerView.Adapter {
        Context context;
        ArrayList<Mot> motArrayList;
        public MotAdapter (Context context, ArrayList<Mot> motArrayList){
            this.context =context;
            this.motArrayList =motArrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View  view = LayoutInflater.from(context).inflate(R.layout.liste_mot_adapter, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (motArrayList!=null && motArrayList.size()>0){
                Mot mot = motArrayList.get(position);
                ViewHolder.mot.setText(mot.getMot());
                ViewHolder.occurrence.setText(""+mot.getOccurence());
                ViewHolder.frequence.setText(""+mot.getFrequence());
            } else {
                return ;
            }
        }

        @Override
        public int getItemCount() {
            return motArrayList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public static TextView mot, occurrence, frequence;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                 mot = (TextView) itemView.findViewById(R.id.id_mot);
                 occurrence = (TextView)itemView.findViewById(R.id.id_occurrence);
                 frequence = (TextView)itemView.findViewById(R.id.id_frequence);
            }
        }
    }

    public static class Mot_Adapter extends RecyclerView.Adapter {

        Context context;
        Map<String,Integer> motArrayList;

        public Mot_Adapter (Context context, Map<String,Integer> motArrayList){
            this.context =context;
            this.motArrayList =motArrayList;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View  view = LayoutInflater.from(context).inflate(R.layout.liste_mot_adapter, parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (motArrayList!=null && motArrayList.size()>0){
                // Decomposition de Map en tableau des mots et leurs occurences
                List<String> mot = new ArrayList<String>(motArrayList.keySet());
                String[] mots = motArrayList.keySet().toArray(new String[0]);
                Integer [] occurence = motArrayList.values().toArray(new Integer[0]);

                float freq = (float)occurence[position].intValue()/occurence.length;
                    DecimalFormat df = new DecimalFormat("#.###");
                    df.setRoundingMode(RoundingMode.HALF_UP);

                    ViewHolder.mot.setText(mots[position]);
                    ViewHolder.occurrence.setText(""+occurence[position].intValue());
                    ViewHolder.frequence.setText(""+df.format(freq));
                /*
                for (Map.Entry<String,Integer> entry :motArrayList.entrySet()){
                    ViewHolder.mot.setText(entry.getKey());
                    ViewHolder.occurrence.setText(""+entry.getValue());
                    ViewHolder.frequence.setText(""+entry.getValue()/motArrayList.size());
                }
                 */
            } else {
                return ;
            }
        }

        @Override
        public int getItemCount() {
            return motArrayList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public static TextView mot, occurrence, frequence;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                mot = (TextView) itemView.findViewById(R.id.id_mot);
                occurrence = (TextView)itemView.findViewById(R.id.id_occurrence);
                frequence = (TextView)itemView.findViewById(R.id.id_frequence);
            }
        }
    }
    public static String Clean_texte(String texte){
        String text;
        // convertir les majuscule en minuscule
        text = texte.toLowerCase();
        // remplacer les caracteres non lettres par de l'espace
        text = text.replaceAll("[^a-zA-Zéêèàâçûùôî]+"," ").trim();
        text=remove_stop_word(text);

        return text;
    }

    public static String remove_stop_word(String text){
        String stop_word = "a z e r t y u i o p q s d f g h j k l m w x c v b n à demi peine peu près absolument actuellement ainsi alors apparemment approximativement après demain assez assurément au aucun aucunement aucuns aujourd\\'hui auparavant aussi aussitôt autant autre autrefois autrement avant avec avoir beaucoup bien bientôt bon c ça car carrément ce cela cependant certainement certes ces ceux chaque ci comme comment complètement d abord dans davantage de début dedans dehors déjà demain depuis derechef des désormais deux devrait diablement divinement doit donc dorénavant dos droite drôlement du elle elles en vérité encore enfin ensuite entièrement entre temps environ essai est et étaient état été étions être eu extrêmement fait faites fois font force grandement guère habituellement haut hier hors ici il ils infiniment insuffisamment jadis jamais je joliment ka la là le les leur leurs lol longtemps lors ma maintenant mais mdr même mes moins mon mot naguère ne ni nommés non notre nous nouveaux nullement ou où oui par parce parfois parole pas mal passablement personne personnes peu peut être pièce plupart plus plutôt point pour pourquoi précisément premièrement presque probablement prou puis quand quasi quasiment que quel quelle quelles quelque quelquefois quels qui quoi quotidiennement rien rudement s sa sans sans doute ses seulement si sien sitôt soit son sont soudain sous souvent soyez subitement suffisamment sur t ta tandis tant tantôt tard tellement tellement tels terriblement tes ton tôt totalement toujours tous tout toutefois très trop tu un une valeur vers voie voient volontiers vont votre vous vraiment vraisemblablement";
        Mots_vides=stop_word.split(" ");

        //text = text.trim().replaceAll("\\s"," ");
        String [] words = text.split(" ");

        for (String word : words){
            wordList.add(word);
        }

        // remove stop words
        for (int i=0; i<wordList.size();i++){
            for (int j=0; j< Mots_vides.length; j++){
                if(wordList.contains(Mots_vides[j])){
                    wordList.remove(Mots_vides[j]);
                }
            }
        }

        String string = "" ;
        for ( String s : wordList)
        {
            string +=s + " ";
        }
        //text = string; wordList.toString();
        // text= text.replaceAll(",|\\[|\\]"," ").trim();
        //text=text.replaceAll(" +"," ");
        return string;
    }
}
