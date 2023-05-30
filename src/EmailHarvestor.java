import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EmailHarvestor {

	private Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		try {
			System.out.print("\033[H\033[2J");  
			System.out.flush();
			EmailHarvestor emailHarvestor = new EmailHarvestor();
			boolean flag = false;
			emailHarvestor.displayBanner();
			emailHarvestor.initialInputs(args);
			
		} catch(Exception e) {
			System.out.println("[e] "+e.getLocalizedMessage());
		}
	}
	
	public void initialInputs(String[] args) {
		Queue<String> queuedURLs = new LinkedList<>();
		System.out.println("\n\n[+] Enter File Path with File Name containing target URLs : ");
		String filePath = scanner.nextLine();
		queuedURLs = readURLFiles(filePath);
		
		while(queuedURLs.isEmpty()!=true){
			System.out.println("[p] Harvesting " + queuedURLs.peek());
			connectURLsAndGetEmails(queuedURLs.poll());
		}
		
		exitOrRestart(args);
	}
	
	public void exitOrRestart(String[] args) {
		System.out.println("\n\n[+] Type R to Restart, E to Exit");
		String s = scanner.next();
		if (s.equals("R")) {
			main(args);
		} else if(s.equals("E")) {
			System.out.println("Good Bye!");
			System.exit(0);
		} else {
			exitOrRestart(args);
		}
	}

	public Queue<String> readURLFiles(String filePath) {
		System.out.println("[p] Reading URLs");
		Queue<String> queue = new LinkedList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				queue.add(line);
			}
		} catch (IOException e) {
			System.out.println("[e] " +e.getLocalizedMessage());
		}
		return queue;
	}

	public Set<String> getEmailsFromHtml(Document doc) {
		System.out.println("[p] Getting email from html");
		Set<String> emails = new HashSet<>();

		// Extract email addresses from text nodes
		String text = doc.text();
		String[] words = text.split("\\s+");
		for (String word : words) {
			if (word.contains("@") && word.contains(".")) {
				emails.add(word);
			}
		}

		// Extract email addresses from anchor elements (links)
		Elements links = doc.select("a[href]");
		for (Element link : links) {
			String href = link.attr("href");
			if (href.startsWith("mailto:")) {
				String email = href.substring("mailto:".length());
				emails.add(email);
			}
		}

		return emails;
	}

	public void connectURLsAndGetEmails(String url){
		try {
            Document doc = Jsoup.connect(url).get();
            Set<String> emails = getEmailsFromHtml(doc);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(System.getProperty("user.dir") + "\\harvested-emails-"+System.currentTimeMillis()+".txt"))) {
            	boolean flag = false;
            	for (String email : emails) {
            		if (flag==false) {
            			email = url + "\n" + email;
            			flag = true;
            		}
                    writer.write(email);
                    writer.newLine();
                }
            	System.out.println("[i] Total " + emails.size() + " harvested from " + url);
			}
		}
         catch (IOException e) {
 			System.out.println("[e] " +e.getLocalizedMessage());
        }
	}
	
	
	public void displayBanner() {
        
//		System.out.print("\u001B[31m");
		
		String os = "";
		try {
            os = System.getProperty("os.name").toLowerCase();

            // Check if the operating system is Windows
            if (os.contains("win")) {
                // Use 'cmd.exe' to execute Windows-specific color command
                ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "color 0A");
                processBuilder.inheritIO();
                Process process = processBuilder.start();
                process.waitFor();
            }

            // Print colored text
            if(!os.contains("7"))
            System.out.println("\u001B[31m");
        } catch (Exception ex) {
			System.out.println("[e] " +ex.getLocalizedMessage());
        }
		
        String[] frames = {
                "███████╗███╗   ███╗ █████╗ ██╗██╗         ██╗  ██╗ █████╗ ██████╗ ██╗   ██╗███████╗███████╗████████╗ ██████╗ ██████╗ ",
                "██╔════╝████╗ ████║██╔══██╗██║██║         ██║  ██║██╔══██╗██╔══██╗██║   ██║██╔════╝██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗",
                "█████╗  ██╔████╔██║███████║██║██║         ███████║███████║██████╔╝██║   ██║█████╗  ███████╗   ██║   ██║   ██║██████╔╝",
                "██╔══╝  ██║╚██╔╝██║██╔══██║██║██║         ██╔══██║██╔══██║██╔══██╗╚██╗ ██╔╝██╔══╝  ╚════██║   ██║   ██║   ██║██╔══██╗",
                "███████╗██║ ╚═╝ ██║██║  ██║██║███████╗    ██║  ██║██║  ██║██║  ██║ ╚████╔╝ ███████╗███████║   ██║   ╚██████╔╝██║  ██║",
                "╚══════╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝╚══════╝    ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝  ╚═══╝  ╚══════╝╚══════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝",
                "A product by Hardik Rawat                                                                                      v1.0.0"
        };
        System.out.println();
        for (int i = 0; i < 7; i++) {
            System.out.println(frames[i % frames.length]);
            try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				System.out.println("[e] " +e.getLocalizedMessage());
			}
        }

        System.out.println("_____________________________________________________________________________________________________________________");
        
        if(!os.contains("7")) {
        System.out.print("\u001B[0m");
        }
    }
}
