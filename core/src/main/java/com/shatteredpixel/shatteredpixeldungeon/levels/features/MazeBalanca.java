package com.shatteredpixel.shatteredpixeldungeon.levels.features;

import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.watabou.utils.Random;

public class MazeBalanca {

    private final int N = 2333;
    private boolean[][] sta = new boolean[N][N];
    private boolean[][][] pat = new boolean[N][N][2];   // down & right
    private int n, m, dd, dm, di, thr; // n row && m line && dd = defined distance
    private String map_in_str = "";

    private String[] paint_buf =new String[4];
    private String paint_pFull() {
        StringBuilder tmp = new StringBuilder();
        tmp.append("WWWW");
        for (int i = 1; i <= m; ++i)    tmp.append("WWWW");
        tmp.append("W\n");
        for (int i = 0; i <= n; ++i) {
            for (int j = 0; j <= 3; ++j) paint_buf[j] = "W";
            for (int j = 0; j <= m; ++j) {
                paint_buf[0] += "eeeW";
                paint_buf[1] += "eee";
                paint_buf[1] += (pat[i][j][1] ? "  " : "W");
                paint_buf[2] += "eeeW";
                paint_buf[3] += "W";
                paint_buf[3] += (pat[i][j][0] ? "  " : "W");
                paint_buf[3] += "WW";
            }
            for (int j = 0; j <= 3; ++j) tmp.append(paint_buf[j]).append("\n");
        }
        return tmp.toString();
    }

    public int[] paint_pArray(){
        return paint_pArray(map_in_str,(n*4+1)*(m*4+1));
    }

    public int[] paint_pArray(String strmap,int size){     //需要使用已经输出的字符串
        int i=0;
        int[] a = new int[size];
        for(int x=0; x < strmap.length(); x++) {
            char ch = strmap.charAt(x);
            switch (ch){
                case 'W':
                    a[i]=Terrain.WALL;
                    i++;
                    break;
                case 'e':
                    a[i]=Terrain.EMPTY;
                    i++;
                    break;
            }
        }
        return a;
    }

    private boolean FindR_check(int x, int y) {
        return x >= 0 && x <= n && y >= 0 && y <= m;
    }

	private int[] FindR_mx = { -1, 1, 0, 0 }, FindR_my = {0,0,1,-1};//{ 0, 0, -1, 1 };

    private boolean FindR_sr(int x, int y, int dis) {
        //printf("%d %d %d\n", x, y, dis);
        //putchar('\n');
        //paint::pai();
        sta[x][y] = true;
        if (x == n && y == m) {
            if (di <= dis && dis <= dm) {
                pat[x][y][0] = true;
                dd = dis;
                return true;
            }
            else {
                sta[x][y] = false;
                return false;
            }
        }
        else if (dis + n - x + m - y < dm) {
            boolean[] acc = new boolean[4];
            acc[0] = false;

            int rad, nx, ny;
            while (!(acc[0] && acc[1] && acc[2] && acc[3])) {
                rad = Random.Int(0,4) % 4;
                nx = x + FindR_mx[rad];
                ny = y + FindR_my[rad];
                if (FindR_check(nx, ny) && !sta[nx][ny] && FindR_sr(nx, ny, dis + 1)) {
                    if (rad == 0)   pat[nx][ny][0] = true;
                    if (rad == 1)   pat[x][y][0] = true;
                    if (rad == 2)   pat[nx][ny][1] = true;
                    if (rad == 3)   pat[x][y][1] = true;
                    return true;
                }
                acc[rad] = true;
            }
        }
        sta[x][y] = false;
        return false;
    }


    private static int CTW_cot = 0;
    private void CTW_main() {
        boolean CTW_hori = Random.Int(0, 1) == 1;
        int CTW_ro = n + 1 - (CTW_hori ? 1:0), CTW_li = m + 1 - (CTW_hori ? 0:1);
        int CTW_al = Random.Int(0,CTW_li-1), CTW_ar = Random.Int(0,CTW_ro-1);
        int CTW_bl = CTW_al + (CTW_hori ? 0:1), CTW_br = CTW_ar + (CTW_hori ? 1:0);
        if (sta[CTW_ar][CTW_al] ^ sta[CTW_br][CTW_bl]) {
            pat[CTW_ar][CTW_al][(CTW_hori ? 0:1)] = true;
            sta[CTW_ar][CTW_al] = sta[CTW_br][CTW_bl] = true;
            ++thr;
            //printf("%d %d %d %d %d\n", ar, al, br, bl, thr);
            //paint::pai();
        }
        else {
            ++CTW_cot;
            //if (cot > 1000 && ar == 3 && al == 3)
            //printf("%d %d %d %d\n", ar, al, br, bl);
        }
    }

    public MazeBalanca(int n, int m){
        this(n,m,n*m,n+m);
    }

    public MazeBalanca(int width,int height,int dmax,int dmin) {
        dm = dmax; di = dmin;
        n = width; m = height;
        n--; m--;
        FindR_sr(0, 0, 1);
        thr = dd;
        while (thr < (n + 2) * (m + 2)) CTW_main();
        map_in_str = paint_pFull();
    }

    public int width(){
        return 4 * n + 1;
    }
}
