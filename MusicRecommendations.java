import java.io.IOException;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.commons.lang3.StringUtils;
import java.util.*;
public class MusicRecommendations {

	public static void main(String[] args) throws InterruptedException {
		
		
		String buffer;
		String refreshOrQuit="r";
		ArrayList<StringBuffer> availableSongs=new ArrayList<StringBuffer>();
		Random rand=new Random();
		Scanner scan=new Scanner(System.in);
		String htmlCode; 
		Document doc=null; 
		String songAlbumPlaylist="";
		System.out.println("Hello! This app will recommend you either a song, album, or playlist ");
		System.out.println("based on three artists you like and how you're feeling.");
		Thread.sleep(1000);
		System.out.println("Would you like a song, album, or playlist?");
		while (!songAlbumPlaylist.contentEquals("song") && !songAlbumPlaylist.contentEquals("album") && 
				!songAlbumPlaylist.contentEquals("playlist")) {
			songAlbumPlaylist=scan.nextLine();
			songAlbumPlaylist=songAlbumPlaylist.toLowerCase();
			if (!songAlbumPlaylist.contentEquals("song") && !songAlbumPlaylist.contentEquals("album") &&
					!songAlbumPlaylist.contentEquals("playlist")) {
				System.out.println("Invalid input");
			}
		}
		ArrayList<String> genreText=new ArrayList<>();
		genreText.add("Looks like you're a fan of ");genreText.add("You seem to enjoy ");genreText.add("You must like ");
		System.out.println("Okay, now, name three artists you like.");
		int numOfArtists=3;
		String currMood="";
		String currGenre="";
		String currArtist="";
		StringBuffer moodsUrl=new StringBuffer("d:");
		StringBuffer newUrl=new StringBuffer("https://rateyourmusic.com/charts/top/single/all-time/");
		HashSet<String> moods=new HashSet<>();
		moods.add("angry");moods.add("anxious");moods.add("bittersweet");moods.add("calm");moods.add("disturbing");
		moods.add("energetic");moods.add("happy");moods.add("lethargic");moods.add("longing");moods.add("mellow");moods.add("passionate");
		moods.add("quirky");moods.add("romantic");moods.add("sad");moods.add("sensual");moods.add("sentimental");
		moods.add("uplifting");
		HashSet<String> favGenres=new HashSet<>();
		while (numOfArtists!=0) {
			System.out.print("Name an artist: ");
			currArtist=scan.nextLine();
			//Search for artist
			try {
				doc = Jsoup.connect("https://rateyourmusic.com/search?searchterm=" + currArtist + "&searchtype=a")
						.timeout(6000).get();
			} catch (IOException e) {
				System.out.println("Failed to connect to webpage.");
				return;
			}
			htmlCode=doc.toString();

			currGenre=StringUtils.substringBetween(htmlCode,"href=\"/genre/","/");

			try {
				currGenre.length();
			} catch (NullPointerException e) {
				System.out.println("Couldn't find artist. Please try a different one.");
				continue;
			}
			favGenres.add(currGenre);
			System.out.println(genreText.remove(0) + currGenre.replace("-", " ").replace("_", "/"));
			numOfArtists--;
		}
		System.out.println("Good choices. If you could listen to a song right now,");
		System.out.println("how would you want it to sound?\n");
		System.out.println("Possible moods include angry, anxious, bittersweet, calm,\n"
				+ "disturbing, energetic, happy, lethargic, longing, mellow, \n"
				+ "passionate, quriky, romantic, sad, sensual, sentimental, and uplifting.\n"
				+ "Type 'done' when you finish typing what you want.\n");

		while (currMood!="done") {
			currMood=scan.nextLine();
			currMood=currMood.toLowerCase();
			if (currMood.equals("done")) {
				break;
			}
			if (!moods.contains(currMood)) {
				System.out.println("Not a possible mood/already used, try again");
				continue;
			}
			moodsUrl.append(currMood);
			moodsUrl.append(",");
		}
		System.out.println("Thanks! Let's get you your " + songAlbumPlaylist + "...");
		while (refreshOrQuit.equals("r")) {
			newUrl.delete(0,newUrl.length());
			newUrl.append("https://rateyourmusic.com/charts/top/");
			if (songAlbumPlaylist.equals("album")) {
				newUrl.append("album");
			} else {
				newUrl.append("single");
			}
			newUrl.append("/all-time/");
			newUrl.append("g:");
			for (String genre : favGenres) {
				newUrl.append(genre);
				newUrl.append(",");
			}
			newUrl.append("/");
			newUrl.append(moodsUrl);
			newUrl.append("/");
			newUrl.append(rand.nextInt(8));
			newUrl.append("/#results");
			StringBuffer currData=new StringBuffer("");
			try {
				doc = Jsoup.connect(newUrl.toString()).timeout(6000).get();
			} catch (IOException e) {
				System.out.println("Failed to connect to webpage.");
				return;
			}
			Elements songs=doc.getElementsByTag("a");
			for (Element e : songs) {
				if (e.getElementsByAttribute("title").text().length()!=0 || e.getElementsByClass("artist").text().length()!=0) {
					if (e.getElementsByClass("artist").text().length()==0) {
						availableSongs.add(currData);
						currData=new StringBuffer("");
					}
					currData.append(e.getElementsByAttribute("title").text());
					if (e.getElementsByAttribute("title").text().length()!=0) {
						currData.append(" - ");
					}
					currData.append(e.getElementsByClass("artist").text());
				}
			}
			if (availableSongs.size()<10) {
				Thread.sleep(1000);
				continue;
			}
			System.out.println("We recommend you try...");

			if (songAlbumPlaylist.equals("playlist")) {
				for (int i=0; i<25; i++) {
					try {
						buffer=availableSongs.remove(rand.nextInt(availableSongs.size())).toString();
					} catch (IllegalArgumentException e) {
						continue;
					}
					if (buffer.length()!=0 && !Character.isDigit(buffer.charAt(0)) && !buffer.contains("Next -") && !buffer.contains("Prev -")) {
						System.out.println(buffer);
					}
				}
			} else {
				buffer=availableSongs.get(rand.nextInt(availableSongs.size())).toString();
				if (buffer.length()!=0 && !Character.isDigit(buffer.charAt(0)) && !buffer.contains("Next -") && !buffer.contains("Prev -")) {
					System.out.println(buffer);
				}
			}

			availableSongs.clear();
			Thread.sleep(1000);
			System.out.println("Type r to refresh and get more recommendations, or q to quit.");
			refreshOrQuit="";
			
			while (!refreshOrQuit.equals("r") && !refreshOrQuit.equals("q")) {
				refreshOrQuit=scan.nextLine();
				if (!refreshOrQuit.equals("r") && !refreshOrQuit.equals("q")) {
					System.out.println("Invalid input");
				}
			}
			if (refreshOrQuit.equals("q")) {
				return;
			}

		}
		scan.close();
	}
	
}
