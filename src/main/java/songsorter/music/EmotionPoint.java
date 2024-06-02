package songsorter.music;

/**
 * Objct to represent the emotion state of a song on Russel's Circumplex Model of Affect
 * @see <a href="https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2367156/">Russel's Circumplex Model of Affect</a>
 */
public class EmotionPoint {

    private double arousal, valence; 

    /**
     * Constructor with specified arousal and valence values
     * @param a arousal
     * @param v valence
     */
    public EmotionPoint(double a, double v) {
        arousal = a; 
        valence = v; 
    }

    /**
     * @return the arousal of the {@code EmotionPoint} object
     */
    public double getArousal() {
        return this.arousal;
    }

    /**
     * @return the valence of the {@code EmotionPoint} object
     */
    public double getValence() {
        return this.valence;
    }

    @Override
    public String toString() {
        return "<A:"+arousal+", V:"+valence+">"; 
    }

    /**
     * Method to convert an {@code EmotionPoint} to a interpretable emotion as per
     * Figure 1 from Yik, M., Russell, J.A., & Steiger, J.H. (2011). A 12-Point 
     * Circumplex Structure of Core Affect. Emotion, 11 4, 705-31. 
     * @return String emotion
     */
    public String asEmotion() {
        double theta = Math.toDegrees(Math.atan(arousal/valence));

        if (theta > 345 || theta <= 15) {
            return "Satisfied"; 
        }
        else if (theta > 15 && theta <= 45) {
            return "Enthusiastic"; 
        }
        else if (theta > 45 && theta <= 75) {
            return "Energetic"; 
        }
        else if (theta > 75 && theta <= 105) {
            return "Activated"; 
        }
        else if (theta > 105 && theta <= 135) {
            return "Frenzied"; 
        }
        else if (theta > 135 && theta <= 165) {
            return "Distressed";
        }
        else if (theta > 165 && theta <= 195) {
            return "Unhappy";
        }
        else if (theta > 195 && theta <= 225) {
            return "Sad";
        }
        else if (theta > 225 && theta <= 255) {
            return "Sluggish";
        }
        else if (theta > 255 && theta <= 285) {
            return "Quiet";
        }
        else if (theta > 285 && theta <= 315) {
            return "Placid";
        }
        else if (theta > 315 && theta <= 345) {
            return "Serene";
        }
        else {
            return "Unknown"; 
        }
    }
}
