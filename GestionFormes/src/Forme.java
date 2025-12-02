// Auteur : Ahmed Boukra Bettayeb 
// Version : 1.0
// Date : 2025-11-25
// Classe abstraite Forme : compteur global + liste dynamique centralisée

import java.util.ArrayList;
import java.util.List;

public abstract class Forme {
    // compteur global de toutes les formes
    protected static int compteurFormes = 0;
    
    // Liste dynamique centralisée - stocke TOUTES les formes peu importe le type
    private static final List<Forme> listeForme = new ArrayList<>();

    protected Forme() {
        incrementerFormes();
    }

    protected static void incrementerFormes() {
        compteurFormes++;
    }

    protected static void decrementerFormes() {
        if (compteurFormes > 0) compteurFormes--;
    }

    // ============ GESTION DE LA LISTE DYNAMIQUE CENTRALISÉE ============
    public static void ajouterAListe(Forme f) {
        listeForme.add(f);
    }
    
    public static void retirerDeListe(Forme f) {
        listeForme.remove(f);
    }
    
    public static int getTailleListe() {
        return listeForme.size();
    }
    
    public static Forme getFormeAt(int idx) {
        if (idx >= 0 && idx < listeForme.size()) {
            return listeForme.get(idx);
        }
        return null;
    }
    
    public static List<Forme> getListeForme() {
        return listeForme;
    }

    // Methode appelee lorsque l utilisateur supprime explicitement la forme
    // Chaque sous-classe DOIT appeler super.detruireFormeGlobale() pour decrementer
    public void detruireFormeGlobale() {
        decrementerFormes();
    }

    public static int getCompteurFormes() {
        return compteurFormes;
    }

    // Contrat que doivent implementer les formes concretes
    public abstract double getSurface();
    public abstract double getPerimetre();

    // methode optionnelle de saisie ; les classes concretes proposeront
    public abstract void saisirDimensions();              // saisie interactive
    public void saisirDimensions(double a) { /* optional */ }
    public void saisirDimensions(double a, double b) { /* optional */ }
    public void saisirDimensions(double a, double b, double c) { /* optional */ }

    // Retourne le nom de la classe (pour affichage)
    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
    // Méthode par défaut pour détruire la forme :
    // les sous-classes peuvent surcharger pour décrémenter
    // leur propre compteur puis appeler super.detruire().
    public void detruire() {
        detruireFormeGlobale();
    }
}

