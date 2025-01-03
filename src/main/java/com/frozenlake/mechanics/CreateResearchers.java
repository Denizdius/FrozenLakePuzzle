package FrozenLakePuzzle;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class CreateResearchers {
    private Queue<String> researchers;

    public CreateResearchers() {
        researchers = new LinkedList<>();
    }

    public void createResearchers() {
        Random random = new Random();
        int numberOfResearchers = random.nextInt(3) + 2; // Random number between 2 and 4
        //System.out.println("Number of Researchers: " + numberOfResearchers);

        for (int i = 1; i <= numberOfResearchers; i++) {
            researchers.add("R" + i);
        }
    }

    public Queue<String> getResearchers() {
        return researchers;
    }
}
