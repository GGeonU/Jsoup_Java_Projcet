package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Scanner;

class GetStatTable {

    String url_avg = "https://www.nba.com/warriors/stats";
    Document doc_avg;
    Elements stat_table;

    String[] info = new String[14];  // 정보
    String[][] st = new String[14][13];   // stats

    Elements getElements() {
        try {
            doc_avg = Jsoup.connect(url_avg).get();
            stat_table = doc_avg.select("table.stats-table.player-stats" +
                    ".season-averages.table.table-striped.table-bordered.sticky-enabled");
        } catch (IOException e) {
            System.out.println("network error");
            e.printStackTrace();
        }

        return stat_table;

    }
}

class GetInfo extends GetStatTable {
    Elements player;

    String[] name = new String[14];
    int[] number = new int[14];

    String[] arr_info = getPlayerInfo();

    @Override
    Elements getElements() {
        super.getElements();
        player = stat_table.select("td.player_name");
        return player;
    }

    String[] getPlayerInfo(){
        Elements e_player = getElements();
        for(int i=0; i<14; i++) {
            String buffer = e_player.get(i).attr("title");
            buffer = buffer.replace(","," ");
            buffer = buffer.replace("Number","");
            buffer = buffer.replace("Guard","").replace("Forward","").
                    replace("Center","").replace("-",""); // position
            info[i] = buffer;
        }
        return info;
    }

    String[] split(String str){
        String array[] = str.split("   ");
        return array;
    }

    String[] get_name(){
        for (int i = 0; i < info.length; i++) {
            name[i] = split(arr_info[i])[0];
        }

        return name;
    }

    int[] get_number() {
        for (int i = 0; i < info.length; i++) {
            number[i] = Integer.parseInt(split(arr_info[i])[1]);
        }

        return number;
    }
}

class GetStat extends GetStatTable{

    Elements stats;

    @Override
    Elements getElements() {
        super.getElements();
        stats = stat_table.select("tr");
        return stats;
    }

    String[][] getStats(){
        for(int i=0; i<14; i++) {
            for(int j=0; j<13; j++) {
                st[i][j] = getElements().get(i + 1).select("td").get(j+1).text();
            }
        }
        return st;
    }
}

class DataSearch{

    int count;

    GetInfo getinfo = new GetInfo();

    String[] name = getinfo.get_name();
    int[] number = getinfo.get_number();

    private String input;

    void setInput(String str){
        input = str;
    }

    String getInput(){
        return input;
    }

    boolean isInput(){
        char temp;
        boolean result = true;
        String buf = getInput();

        for(int i=0; i<buf.length(); i++){
            temp = buf.charAt(i);

            if((int)temp<48 || (int)temp>57){
                result = false;
                break;
            }
        }
        return result;
    }

    int Search(int a){
        for(int i= 0; i<14; i++){
            if(number[i] == a){
                count = i;
                break;
            }
            else
                count = -1;
        }
        return count;
    }

    int Search(String s){
        for(int i=0; i<14; i++){
            if(s.equals(name[i])){
                count = i;
                break;
            }
            else
                count = -1;
        }
        return count;
    }

}

class Rank {

    //String url_rank = "https://www.nba.com/warriors/stats/leaders";

    Document doc_rank;
    String url_rank = "https://www.nba.com/warriors/stats/leaders";
    {
        try {
            doc_rank = Jsoup.connect(url_rank).get();
        } catch (IOException e) {
            System.out.println("Network Error");
            e.printStackTrace();
        }
    }

    String[] rank_pts(){
        String[] rank = new String[3];
        Elements e = doc_rank.select("ul.nav.nav-pills.nav-pills-stats" +
                "-leaders.nav-pills-stats-leaders-PTS");
        Elements name = e.select("div.player-name");
        Elements pts = e.select("div.stat-num");

        for(int i=0; i<3; i++){
            rank[i] = name.get(i).text() + "  PTS: " + pts.get(i).text();
        }
        return rank;
    }

    String[] rank_reb(){
        String[] rank = new String[3];
        Elements e = doc_rank.select("ul.nav.nav-pills.nav-pills-stats-leaders.nav-pills-stats-leaders-REB" );
        Elements name = e.select("div.player-name");
        Elements reb = e.select("div.stat-num");

        for(int i=0; i<3; i++){
            rank[i] = name.get(i).text() + "  REB: " + reb.get(i).text();
        }
        return rank;
    }

    String[] rank_ast(){
        String[] rank = new String[3];
        Elements e = doc_rank.select("ul.nav.nav-pills.nav-pills-stats-leaders.nav-pills-stats-leaders-AST" );
        Elements name = e.select("div.player-name");
        Elements ast = e.select("div.stat-num");

        for(int i=0; i<3; i++){
            rank[i] = name.get(i).text() + "  AST: " + ast.get(i).text();
        }
        return rank;
    }
}

class GetList extends DataSearch{

    private int cnt;
    GetStat getStat = new GetStat();
    String[][] arr = getStat.getStats();

    String[] name = getinfo.get_name();
    int[] number = getinfo.get_number();
    String[][] stats = getStat.getStats();
    String[] list_info = new String[5];
    String[][] list = new String[5][13];

    String[][] add_list(int n) {
        for (int i = 0; i < cnt + 1; i++) {
            list_info[cnt] = "\n "+name[n]+"  "+number[n]+"\n";
            for (int j = 0; j < 13; j++) {
                list[cnt][j] = " "+arr[n][j];
            }
        }
        cnt++;
        return list;
    }

    void print_list(){
        for(int i=0; i<cnt; i++) {
            System.out.println("\n "+list_info[i]);
            System.out.printf(" Game: %-5s", list[i][0]);
            System.out.printf(" PTS: %-6s", list[i][1]);
            System.out.printf(" FG: %-6s", list[i][2]);
            System.out.printf(" FG%%: %-7s", list[i][3]);
            System.out.printf(" 3P%%: %-7s", list[i][4]);
            System.out.printf(" FT%%: %-7s", list[i][5]);
            System.out.printf(" REB: %-6s", list[i][8]);
            System.out.printf(" AST: %-6s", list[i][9]);
            System.out.printf(" STL: %-6s", list[i][10]);
            System.out.printf(" TO: %-6s", list[i][11]);
            System.out.printf(" PF: %-6s", list[i][12]);
            System.out.println("\n");
        }
    }

    void print_all(int n) {
        System.out.println("\n "+name[n]+"  "+number[n]);
        System.out.printf(" Game: %-5s", stats[n][0]);
        System.out.printf(" PTS: %-6s", stats[n][1]);
        System.out.printf(" FG: %-6s", stats[n][2]);
        System.out.printf(" FG%%: %-7s", stats[n][3]);
        System.out.printf(" 3P%%: %-7s", stats[n][4]);
        System.out.printf(" FT%%: %-7s", stats[n][5]);
        System.out.printf(" REB: %-5s", stats[n][8]);
        System.out.printf(" AST: %-6s", stats[n][9]);
        System.out.printf(" STL: %-6s", stats[n][10]);
        System.out.printf(" TO: %-5s", stats[n][11]);
        System.out.printf(" PF: %-5s", stats[n][12]);
        System.out.println("\n\n");
    }

    void print(int n){
        System.out.println("\n "+name[n]+"  "+number[n]);
        System.out.printf(" Game: %-5s", stats[n][0]);
        System.out.printf(" PTS: %-6s", stats[n][1]);
        System.out.printf(" FG%%: %-7s", stats[n][3]);
        System.out.printf(" FT%%: %-7s", stats[n][5]);
        System.out.printf(" REB: %-6s", stats[n][8]);
        System.out.printf(" AST: %-6s", stats[n][9]);
        System.out.println("\n\n");
    }
}


public class Main {
    public static void clrscr(){   //clear
        //Clears Screen in java
        try {
            if (System.getProperty("os.name").contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");
        } catch (IOException | InterruptedException ex) {}
    }

    public static void main(String[] args) throws IOException {
        // write your code here

        Scanner scanner = new Scanner(System.in); // scanner

        DataSearch datasearch = new DataSearch();
        Rank rank = new Rank();
        GetList getlist = new GetList();   // 객체
        String[] r_pts = rank.rank_pts();
        String[] r_ast = rank.rank_ast();
        String[] r_reb = rank.rank_reb();    // leader

        int count;

        for (;;) {
            clrscr();
            System.out.println("\n Golden State Warriors Stats Program\n");
            System.out.println(" 사용법:\n");
            System.out.println(" show: 선수 전체의 기록을 보여줍니다 \n");
            System.out.println(" show -a: 선수 전체의 기록과 세부스텟을 보여줍니다 \n");
            System.out.println(" search: 이름이나 등번호로 선수를 검색합니다 \n");
            System.out.println(" rank: 팀 내 선택한 부분의 리더들을 보여줍니다");
            System.out.println("  -p: 득점  -a: 어시스트  -r: 리바운드 \n");
            System.out.println(" list: 원하는 선수를 리스트로 보여줍니다 (최대 5명)");
            System.out.println("  -a: 리스트에 추가   -s: 리스트 보여줌 \n");
            System.out.println(" exit: 종료\n");

            System.out.print("\n\n >> ");
            String input_s = scanner.nextLine();

            if (input_s.equals("show")) {
                clrscr();
                System.out.println();
                for (int i = 0; i < 14; i++) {
                    getlist.print(i);
                }
                System.in.read();
            }

            else if (input_s.equals("show -a")) {
                clrscr();
                for (int i = 0; i < 14; i++) {
                    getlist.print_all(i);
                }

                System.in.read();
            }


            else if(input_s.equals("list -a")){
                clrscr();
                System.out.print("\n Name or Number >> ");
                String input = scanner.nextLine();
                clrscr();
                datasearch.setInput(input);
                boolean b = datasearch.isInput();
                if(b == true) {
                    int n;
                    n = Integer.parseInt(input);
                    count = datasearch.Search(n);
                    if (count == -1) {
                        System.out.println("\n Error");
                        continue;
                    }
                    clrscr();
                    getlist.add_list(count);
                    System.out.println("\n Okay");
                    System.in.read();
                }

                else if(b == false){
                    count = datasearch.Search(input);
                    if (count == -1) {
                        System.out.println("\n Error");
                        continue;
                    }
                    clrscr();
                    getlist.add_list(count);
                    System.out.println("\n Okay");
                    System.in.read();
                }
            }

            else if(input_s.equals("list -s")){
                clrscr();
                System.out.println("\n Favorites List");
                getlist.print_list();
                System.in.read();
            }

            else if (input_s.equals("search")) {
                clrscr();
                System.out.print("\n Name or Number >> ");
                String input = scanner.nextLine();
                clrscr();
                datasearch.setInput(input);
                boolean b = datasearch.isInput();

                if (b == true) {
                    int n;
                    n = Integer.parseInt(input);
                    count = datasearch.Search(n);
                    if (count == -1) {
                        System.out.println("Error");
                        System.in.read();
                        continue;
                    }
                    System.out.println();
                    getlist.print_all(count);
                    System.out.println("\n");

                } else if (b == false) {
                    count = datasearch.Search(input);
                    if (count == -1) {
                        System.out.println("Error");
                        System.in.read();
                        continue;
                    }
                    clrscr();
                    System.out.println();
                    getlist.print_all(count);
                    System.out.println("\n");
                }
                System.in.read();
            }

            else if (input_s.equals("rank -p")) {
                clrscr();
                System.out.println("\n Point Leader");
                for (int i = 0; i < 3; i++) {
                    System.out.println("\n "+(i + 1) + ". " + r_pts[i] + "\n");
                }
                System.in.read();
            }

            else if (input_s.equals("rank -a")) {
                clrscr();
                System.out.println("\n Assist Leader");
                for (int i = 0; i < 3; i++) {
                    System.out.println("\n "+(i + 1) + ". " + r_ast[i] + "\n");
                }
                System.in.read();
            }

            else if (input_s.equals("rank -r")) {
                clrscr();
                System.out.println("\n Rebound Leader");
                for (int i = 0; i < 3; i++) {
                    System.out.println("\n "+(i + 1) + ". " + r_reb[i] + "\n");
                }
                System.in.read();
            }
            else if(input_s.equals("exit")){
                break;
            }

            else{
                System.out.println("다시 입력해주세요");
            }
        }
    }
}
