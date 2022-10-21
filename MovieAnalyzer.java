
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MovieAnalyzer {
    public static Map<String,Integer> Genre = new HashMap<>();
    public static class Movie
    {
        private String Series_Title;
        private int Released_Year;
        private String Certificate;
        private int Runtime;
        private String Genre;
        private double IMDB_Rating;
        private String Overview;
        private String Meta_score;
        private String Director;
        private String Star1;
        private String Star2;
        private String Star3;
        private String Star4;
        private String Noofvotes;
        private int Gross;
        private int ovLen;

        public Movie(String Series_Title, int Released_Year, String Certificate ,String Runtime,
        String Genre,String IMDB_Rating,String Overview,String Meta_score,String Director,String Star1,
                     String Star2,String Star3,String Star4,String Noofvotes,String Gross,String[] all)
        {
            this.Certificate=Certificate;
            this.Director = Director;
            this.Genre = Genre;
            if (Gross==null| Gross.equals("")){
                this.Gross = -1;
            }else {
                this.Gross = Integer.parseInt(Gross.replaceAll(",",""));
            }
            this.IMDB_Rating =Double.parseDouble(IMDB_Rating);
            this.Meta_score = Meta_score;
            this.Noofvotes = Noofvotes;
            this.Overview = Overview;
            this.Released_Year = Released_Year;
            this.Runtime = Integer.parseInt(Runtime.substring(0,Runtime.length()-4));
            this.Series_Title = Series_Title;
            this.Star1 = Star1;
            this.Star2 = Star2;
            this.Star3 = Star3;
            this.Star4 = Star4;
            this.ovLen = Overview.length();
            if (Overview.charAt(0) == '\"'){
                this.ovLen = Overview.length()-2;
            }
//            System.out.println(Series_Title+" = "+ovLen+" = "+Overview);
        }
        public int getOvLen(){return ovLen;}

        public String getSeries_Title() {
            return Series_Title;
        }

        public int getReleased_Year() {
            return Released_Year;
        }

        public String getCertificate() {
            return Certificate;
        }

        public int getRuntime() {
            return Runtime;
        }

        public String getGenre() {
            return Genre;
        }

        public double getIMDB_Rating() {
            return IMDB_Rating;
        }

        public String getOverview() {
            return Overview;
        }

        public String getMeta_score() {
            return Meta_score;
        }

        public String getDirector() {
            return Director;
        }

        public String getStar1() {
            return Star1;
        }

        public String getStar2() {
            return Star2;
        }

        public String getStar3() {
            return Star3;
        }

        public String getStar4() {
            return Star4;
        }

        public String getNoofvotes() {
            return Noofvotes;
        }

        public int getGross() {
            return Gross;
        }

        @Override
        public String toString() {
            return "Movie{" +
                    "Series_Title='" + Series_Title + '\'' +
                    ", Released_Year=" + Released_Year +
                    ", Certificate='" + Certificate + '\'' +
                    ", Runtime='" + Runtime + '\'' +
                    ", Genre='" + Genre + '\'' +
                    ", IMDB_Rating='" + IMDB_Rating + '\'' +
                    ", Overview='" + Overview + '\'' +
                    ", Meta_score='" + Meta_score + '\'' +
                    ", Director='" + Director + '\'' +
                    ", Star1='" + Star1 + '\'' +
                    ", Star2='" + Star2 + '\'' +
                    ", Star3='" + Star3 + '\'' +
                    ", Star4='" + Star4 + '\'' +
                    ", Noofvotes='" + Noofvotes + '\'' +
                    ", Gross='" + Gross + '\'' +
                    '}';
        }
    }
    Stream<Movie> data;
    List<Movie> list = new ArrayList<>();
    public MovieAnalyzer(String dataset_path) throws IOException
    {
        data = Files.lines(Paths.get(dataset_path)).filter(s -> !s.substring(0,2).equals("Po"))
                .map(l -> l.trim().split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1))
                .map(a -> new Movie(a[1].replace("\"",""),
                        Integer.parseInt(a[2].replace("\"","")), a[3].replace("\"",""),
                        a[4].replace("\"",""),a[5].replace("\"",""),
                        a[6].replace("\"",""),a[7],
                        a[8].replace("\"",""),a[9].replace("\"",""),
                        a[10].replace("\"",""),a[11].replace("\"",""),
                        a[12].replace("\"",""),a[13].replace("\"",""),
                        a[14].replace("\"",""),
                        a[15].replace("\"",""),a));
        list = data.collect(Collectors.toList());
    }

    public void reStart() {
        data = list.stream();
    }
    

    public Map<String, Integer> getMovieCountByGenre(){
        reStart();
        Map<String, Integer> map = new TreeMap<>();
        List<Movie> list =
                data.sorted(Comparator.comparing(Movie::getGenre))
                        .collect(Collectors.toList());
        for (Movie movie : list){
            String[] genres;
            genres = movie.Genre.split(",");
            for (String genre : genres) {
                genre = genre.trim();
                if (!map.containsKey(genre)) {
                    map.put(genre, 1);
                } else {
                    int vl = map.get(genre);
                    map.put(genre, vl + 1);
                }
            }
        }
//        Map<String,Integer> map2 = new HashMap<>();
//        for(Map.Entry<String, Long> entry : map1.entrySet()){
//            String mapKey = entry.getKey();
//            Long mapValue = entry.getValue();
//            map2.put(mapKey,mapValue.intValue());
//        }
        List<Map.Entry<String, Integer>> list2 = new ArrayList<>(map.entrySet());
        list2.sort(new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list2.stream().
                collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public Map<Integer, Integer> getMovieCountByYear() {
        reStart();
        Map<Integer, Integer> map = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -o1.compareTo(o2);
            }
        });
        Map<Integer,Long> map1 =
                data.sorted(Comparator.comparing(Movie::getReleased_Year,Comparator.reverseOrder()))
                        .collect(Collectors.groupingBy(Movie::getReleased_Year, Collectors.counting()));
        for(Map.Entry<Integer, Long> entry : map1.entrySet()){
            Integer mapKey = entry.getKey();
            Long mapValue = entry.getValue();
            map.put(mapKey,mapValue.intValue());
        }
        return map;
    }
    
    public Map<List<String>, Integer> getCoStarCount(){
        reStart();
        Map<List<String>, Integer> map = new HashMap<>();
        data.forEach(m -> {
            String[] stars = {m.getStar1(), m.getStar2(), m.getStar3(), m.getStar4()};
            Arrays.sort(stars);
            for (int i = 0; i < stars.length; i++) {
                for (int j = i + 1; j < stars.length; j++) {
                    List<String> list = new ArrayList<>();
                    list.add(stars[i]);
                    list.add(stars[j]);
                    if (map.containsKey(list)) {
                        map.put(list, map.get(list) + 1);
                    } else {
                        map.put(list, 1);
                    }
                }
            }
        });
        List<Map.Entry<List<String>, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(new Comparator<Map.Entry<List<String>, Integer>>() {
            @Override
            public int compare(Map.Entry<List<String>, Integer> o1, Map.Entry<List<String>, Integer> o2) {
                return o2.getValue() - o1.getValue();
            }
        });
        return list.stream().collect(LinkedHashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll);
    }

    public List<String> getTopMovies(int top_k, String by) {
        reStart();
        List<Movie> list = new ArrayList<>();
        List<String> ansList = new ArrayList<>();
        if (by.equals("runtime")){
            list = data.sorted(Comparator.comparing(Movie::getSeries_Title))
                    .sorted(Comparator.comparing(Movie::getRuntime).reversed())
                    .collect(Collectors.toList()).subList(0,top_k);
            for (int i = 0; i < list.size(); i++) {
                ansList.add(list.get(i).Series_Title);
            }
        }else if (by.equals("overview")){
            list = data.sorted(Comparator.comparing(Movie::getSeries_Title))
                    .sorted(Comparator.comparing(Movie::getOvLen).reversed())
                    .collect(Collectors.toList()).subList(0,top_k);
            for (int i = 0; i < list.size(); i++) {
                ansList.add(list.get(i).Series_Title);
            }
        }
        return ansList;
    }
    public List<String> getTopStars(int top_k, String by) {
        reStart();
        List<String> ans = new ArrayList<>();
        List<Movie> movies = data.collect(Collectors.toList());
        if (by.equals("rating")) {
            ArrayList<String> nameArray = new ArrayList<>();
        } else if (by.equals("gross")) {
            ArrayList<String> nameArray = new ArrayList<>();
            for (Movie movie : movies) {
                if (!nameArray.contains(movie.Star1)) {
                    nameArray.add(movie.Star1);
                }
                if (!nameArray.contains(movie.Star2)) {
                    nameArray.add(movie.Star2);
                }
                if (!nameArray.contains(movie.Star3)) {
                    nameArray.add(movie.Star3);
                }
                if (!nameArray.contains(movie.Star4)) {
                    nameArray.add(movie.Star4);
                }
            }
            ArrayList<Person> stars_list = new ArrayList<>();
            for (String star_name : nameArray) {
                Person star = new Person();
                star.name = star_name;
                for (Movie movie : movies) {
                    if (movie.Star1.equals(star_name) || movie.Star2.equals(star_name)
                            || movie.Star3.equals(star_name) || movie.Star4.equals(star_name)) {
                        if (movie.Gross != -1) {
                            star.gross += movie.Gross;
                            star.count++;
                        }
                    }
                }
                if (star.count !=0 ){
                    star.avgross = star.gross / star.count;
                    stars_list.add(star);
                }
            }
            stars_list.sort(new Comparator<Person>() {
                @Override
                public int compare(Person o1, Person o2) {
                    if (o1.avgross == o2.avgross) {
                        return o1.name.compareTo(o2.name);
                    }
                    if (o1.avgross > o2.avgross) {
                        return -1;
                    } else {
                        return 1;
                    }

                }
            });
            for (int i = 0; i < top_k; i++) {
                ans.add(stars_list.get(i).name);
            }
        }
        return ans;
    }

    public List<String> searchMovies(String genre, float min_rating, int max_runtime){
        reStart();
        List<Movie> list = new ArrayList<>();
        List<String> ansList = new ArrayList<>();
        list = data.sorted(Comparator.comparing(Movie::getSeries_Title))
                .filter(s->s.IMDB_Rating>=min_rating &&
                        s.Runtime<=max_runtime&&s.Genre.contains(genre)).collect(Collectors.toList());
        for (Movie movie : list) {
            ansList.add(movie.Series_Title);
        }
        return ansList;
    }

//    public static void main(String[] args) throws IOException {
//        MovieAnalyzer movieAnalyzer = new MovieAnalyzer("resources/imdb_top_500.csv");
////        System.out.println(movieAnalyzer.getMovieCountByYear());
////        System.out.println(movieAnalyzer.getMovieCountByGenre());
////        System.out.println(movieAnalyzer.getTopMovies(500,"runtime"));
////        System.out.println(movieAnalyzer.getTopMovies(50,"overview"));
////        System.out.println(movieAnalyzer.searchMovies("Sci-Fi",8.2f,200));
//    }
    static class Person {
        String name;
        double gross;
        double avgross;
        int count;
    }
}