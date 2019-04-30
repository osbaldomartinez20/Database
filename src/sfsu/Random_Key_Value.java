package sfsu;

import java.util.Random;

public class Random_Key_Value {

    //This list is used to pick the random values to store in the database.
    private static final String[] val = {"gato", "perro", "burro", "gallina", "gallo", "pollo",
                                    "vaca", "toro", "beserro", "chango", "leon", "tigre",
                                    "paloma", "aguila", "pez", "caballo", "potrillo", "pato",
                                    "mula", "hipopotamo", "chapulin", "conejo", "jirafa", "cuervo",
                                    "gusano", "vivora", "serpiente", "abeja", "libelula", "dragon",
                                    "quetzal", "boa", "gorila", "lobo", "coyote", "murcielago", "garza",
                                    "fresa", "jitomate", "tomatillo", "naranja", "toronja", "lima", "limon",
                                    "manzana", "guamuchil", "guayaba", "fruta", "animal", "verdura", "cebolla",
                                    "aguacate", "brocoli", "tuna", "elefante", "puma", "platano", "camote",
                                    "lechuga", "coliflor", "elote", "maiz", "tortilla", "mole", "tamale",
                                    "frijol", "garbanzo", "puerco", "cerdo", "carpa", "pitaya", "nopal", "arroz",
                                    "emu", "koala", "kanguro", "nutria", "marrano", "leche", "agua", "pozole",
                                    "taco", "burrito", "pai", "paleta", "sabritas", "churro", "fritura", "pulpo",
                                    "frambuesa", "planta", "tequila", "cerveza", "vino", "ponche", "agua fresca"};

    //This list returns a randomly picked value from String[] val.
    public synchronized static String getRandValue() {
        Random rand = new Random();
        return val[rand.nextInt(val.length)];
    }

    //This function returns a random key based on the nanoTime
    public synchronized static String getRandKey() {
        return Long.toString(System.nanoTime());
    }

}
