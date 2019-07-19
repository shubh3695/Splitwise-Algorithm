
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ssaxena36
 */
public class SplitwiseTransaction {

    CandidateInfo candidateInfo;
    List<Node>[] expenseMap;

    public static void main(String[] args) {
        SplitwiseTransaction spl = new SplitwiseTransaction();
        try {
            spl.executeFlow();
        } catch (IOException ex) {
            Logger.getLogger(SplitwiseTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Defines the flow of algorithm to calculate and print the relationships
     *
     * @throws IOException
     */
    public void executeFlow() throws IOException {
        initialize();
        parseExpenses();
        int totalUsers = candidateInfo.getTotalCandidates();
        System.out.println("Resolving Money between " + totalUsers + " Users.");
        resolveMultipleConnections(totalUsers);
        printConnectedComponents(totalUsers);
    }

    /**
     * Adds Money that others owed; Subtracts Money that is owed; Maintains map
     * of how much needs to be manipulated
     *
     * @param totalUsers
     */
    public void resolveMultipleConnections(int totalUsers) {
        int[] deltaMoney = new int[totalUsers];
        for (int user = 0; user < totalUsers; user++) {
            for (Node expense : expenseMap[user]) {
                deltaMoney[user] -= expense.money;
                deltaMoney[expense.otherUser] += expense.money;
            }
        }
        Map<Integer, Integer> receiveMoney = new HashMap<>();
        Map<Integer, Integer> sendMoney = new HashMap<>();
        /* Allocate money to where it belongs and with what corresponding user*/
        for (int i = 0; i < totalUsers; i++) {
            if (deltaMoney[i] < 0) {
                sendMoney.put(i, Math.abs(deltaMoney[i]));
            } else if (deltaMoney[i] > 0) {
                receiveMoney.put(i, deltaMoney[i]);
            }
        }
        initializeGraph(totalUsers);
        for (Map.Entry<Integer, Integer> sender : sendMoney.entrySet()) {
            addResolvedDependencies(sender, receiveMoney);
        }
    }

    /**
     * Find if sender and receiver have a feasible mapping
     *
     * @param sender
     * @param receiveMoney
     */
    private void addResolvedDependencies(Map.Entry<Integer, Integer> sender, Map<Integer, Integer> receiveMoney) {
        int give = sender.getValue();
        for (Map.Entry<Integer, Integer> receiver : receiveMoney.entrySet()) {
            int receive = receiver.getValue();
            if (receive > 0) {
                if (receive < give) {
                    receiver.setValue(0);
                    give -= receive;
                    expenseMap[sender.getKey()].add(new Node(receiver.getKey(), receive));
                } else {
                    receiver.setValue(receive - give);
                    expenseMap[sender.getKey()].add(new Node(receiver.getKey(), give));
                    break;
                }
            }
        }
        sender.setValue(give);
    }

    /**
     * Print Graph with Nodes Connected to it
     *
     * @param users
     */
    public void printConnectedComponents(int users) {
        int total = 0;
        for (int user = 0; user < users; user++) {
            for (Node expense : expenseMap[user]) {
                System.out.println(candidateInfo.getCandidateNamebyId(expense.otherUser) + " owes " + candidateInfo.getCandidateNamebyId(user) + " " + expense.money);
                total  += 1;
            }
        }
        System.out.println("Total Transactions required = "+total);
    }

    /**
     * Simple input parser from JSON
     *
     * @throws IOException
     */
    private void parseExpenses() throws IOException {
        // createMappings("A", "C", 300);
        //     createMappings("A", "B", 35);
        // createMappings("A", "E", 35);
        // createMappings("A", "F", 35);
        // createMappings("B", "D", 40);
        // createMappings("B", "G", 10); 
        //     createMappings("B", "A", 35);
        // = 5  (one failing previously)
        
        // createMappings("A", "B", 100);
        // createMappings("A", "C", 50);
        // createMappings("A", "C", 500);
        // createMappings("B", "A", 150);
        // createMappings("B", "C", 200);
        // createMappings("C", "A", 250);
        // createMappings("C", "B", 200); 
        // = 2
        
        // createMappings("A", "B", 100);
        // createMappings("B", "A", 100);
        // createMappings("A", "C", 500);
        // createMappings("C", "A", 500);
        // createMappings("B", "C", 200);
        // createMappings("C", "B", 200); 
        // = 0
    
        //  createMappings("A", "B", 100);
        // = 1
        
        createMappings("A", "C", 100);
        createMappings("A", "E", 40);
            // createMappings("A", "B", 40);
            // createMappings("B", "C", 40);
            // createMappings("C", "A", 40);
        createMappings("A", "F", 30);
        createMappings("B", "D", 50);
        // = 4 (one failing previously)
    }

    private void createMappings(String paidBy, String paidFor, int moneyPaid) {
        int u = candidateInfo.getCandidateIdByName(paidBy);
        int v = candidateInfo.getCandidateIdByName(paidFor);
        Node node = new Node(v, moneyPaid);
        expenseMap[u].add(node);
    }

    /**
     * private initialiser before the process starts
     */
    private void initialize() {
        candidateInfo = new CandidateInfo();
        this.initializeGraph(Constants.MAX_USERS);
    }

    /**
     * Initialize Graph Adjacency List of this total size
     *
     * @param total size of users
     */
    private void initializeGraph(int total) {
        expenseMap = new ArrayList[total];
        for (int i = 0; i < total; i++) {
            expenseMap[i] = new ArrayList<>();
        }
    }
}

/**
 * Node element of the graph
 *
 * @author ssaxena36
 */
class Node {

    int otherUser, money;

    Node(int v, int wt) {
        this.otherUser = v;
        this.money = wt;
    }
}

/**
 * Logs Candidate Id and Name Improves Array Handling If this was not done then
 * Adjacency List wold have been a map Map handling is less time efficient and
 * more complex
 *
 * @author ssaxena36
 */
class CandidateInfo {

    private final Map<Integer, String> ids;
    private final Map<String, Integer> candidate;
    private int totalCandidates;

    public CandidateInfo() {
        this.totalCandidates = 0;
        this.ids = new HashMap<>();
        this.candidate = new HashMap<>();

    }

    /**
     * retrieves unique id by userName
     *
     * @param userName
     * @return integer ID
     */
    public int getCandidateIdByName(String userName) {
        int id;
        if (candidate.containsKey(userName)) {
            id = candidate.get(userName);
        } else {
            id = totalCandidates;
            candidate.put(userName, totalCandidates);
            ids.put(id, userName);
            totalCandidates += 1;
        }
        return id;
    }

    /**
     * returns human understandable name from unique identifier
     *
     * @param id
     * @return
     */
    public String getCandidateNamebyId(int id) {
        return this.ids.get(id);
    }

    /**
     * @return total users
     */
    public int getTotalCandidates() {
        return this.totalCandidates;
    }
}

/**
 * Some constants that might be required
 *
 * @author ssaxena36
 */
class Constants {

    public static final int MAX_USERS = 100;
}
