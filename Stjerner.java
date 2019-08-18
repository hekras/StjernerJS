
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 * janj@hr365.dk;lindah@hr365.dk
 */
/**
 *
 * @author oldhandmixer
 *
 * Path formular simple x = a1 + a2 * cos(t1); t1 = (t1 + dt1) % (2*PI) y = b1 +
 * b2 * sin(u1); u1 = (u1 + du1) % (2*PI) advanced x = a1 * cos(t1) + a2 *
 * cos(t2); t1 = (t1 + dt1) % (2*PI), t2 = (t2 + dt2) % (2*PI) y = b1 * cos(u1)
 * + b2 * sin(u2); u1 = (u1 + du1) % (2*PI), u2 = (u2 + du2) % (2*PI)
 *
 */
public class Stjerner extends Application {

    final double HEIGHT = 2160.0;
    final double WIDTH = 3840.0;
    final int MAXPARTICLES = 10000;
    final int IDLETIMEOUT = 1000;
    final double LEFTTAB = 400.0;
    final double RIGTHTAB = LEFTTAB - 5.0 + 101.0 * 10.0;
    static int idletimeout = 1000;
    Rectangle rectangles[];
    AnimationTimer timer;
    final Canvas CANVAS = new Canvas(WIDTH, HEIGHT);
    List<PBoom> boom = new ArrayList<>();

    static double heartx[], hearty[];

    final String[] params = {
        "Number of particles",
        "(a1) X path center",
        "(b1) Y path center",
        "(a2) X path radius",
        "(b2) Y path radius",
        "(dt1) path rotation velocity",
        "(du1) path rotation phase",
        "(dt2) path rotation velocity",
        "(pss) Particle start random",
        "(psc) Particle start const",
        "(pbf) Partice burnout factor",
        "(psa) Partice start angle",
        "(psdo1) Partice start angle tick 1",
        "(psdo2) Partice start angle tick 2",
        "(psv) Particle start velocity",
        "Shape",
        "Exit!"};

    NumberFormat format = new DecimalFormat("#.####");
    int numberOfParticles = 500;
    static double nnumberOfParticles = 50;
    // path params
    static double a1, a2, t1, dt1;
    static double na1 = 500.0, na2 = 500.0, ndt1 = 530.0;
    static double b1, b2, u1, du1;
    static double nb1 = 500.0, nb2 = 500.0, ndu1 = 500.0;
    static double t2 = 0, dt2, ndt2 = 500.0;
    // particle velocity vector params
    static double pss, npss = 0.0;
    static double psc, npsc = 100;
    static double psa, npsa = 10;
    static double pso = 0.0;
    static double psdo1 = 0.0, npsdo1 = 0.0;
    static double psdo2, npsdo2 = 200.0;
    static double pbf, npbf = 50;
    static double psv, npsv = 10;
    static int shape;
    static double nshape = 0;

    @Override
    public void start(Stage stage) throws Exception {
        Group root = new Group();
        Scene s = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        // mouse click event handler
        s.setOnMousePressed((MouseEvent e) -> {
            int y = -1;

            // check rectangles witch contains mouse
            for (int i = 0; i < rectangles.length; i++) {
                if (rectangles[i].contains(e.getX(), e.getY())) {
                    y = i;
                    break;
                }
            }

            double newn = e.getX() - LEFTTAB; // returns a value 0-999
            switch (y) {
                case 0:
                    nnumberOfParticles = handleMouseClick(nnumberOfParticles, newn, false);
                    break;
                case 1:
                    na1 = handleMouseClick(na1, newn, true);
                    break;
                case 2:
                    nb1 = handleMouseClick(nb1, newn, true);
                    break;
                case 3:
                    na2 = handleMouseClick(na2, newn, true);
                    break;
                case 4:
                    nb2 = handleMouseClick(nb2, newn, true);
                    break;
                case 5:
                    ndt1 = handleMouseClick(ndt1, newn, true);
                    break;
                case 6:
                    ndu1 = handleMouseClick(ndu1, newn, true);
                    break;
                case 7:
                    ndt2 = handleMouseClick(ndt2, newn, true);
                    break;
                case 8:
                    npss = handleMouseClick(npss, newn, true);
                    break;
                case 9:
                    npsc = handleMouseClick(npsc, newn, true);
                    break;
                case 10:
                    npbf = handleMouseClick(npbf, newn, true);
                    break;
                case 11:
                    npsa = handleMouseClick(npsa, newn, true);
                    break;
                case 12:
                    npsdo1 = handleMouseClick(npsdo1, newn, true);
                    break;
                case 13:
                    npsdo2 = handleMouseClick(npsdo2, newn, true);
                    break;
                case 14:
                    npsv = handleMouseClick(npsv, newn, true);
                    break;
                case 15:
                    nshape = handleMouseClick(nshape, newn, false);
                    break;
                case 16:
                    System.exit(0);
                    break;
            }
            updateTrigeometries();
        });

        // for debug only!
        s.setOnMouseMoved((MouseEvent e) -> {
            idletimeout = IDLETIMEOUT;
        });

        root.getChildren().add(CANVAS);
        stage.setScene(s);
        stage.show();
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);

        updateTrigeometries();

        // init rectangles for parameter mouse click
        rectangles = new Rectangle[params.length];
        for (int i = 0; i < rectangles.length; i++) {
            double x = LEFTTAB;
            double y = 20 + i * 25;
            double w = 1000;
            double h = 20;
            rectangles[i] = new Rectangle(x, y, w, h);
        }

        // init collection of particles
        PVector center = new PVector(WIDTH / 2, HEIGHT / 2);
        for (int ii = 0; ii < MAXPARTICLES; ii++) {
            boom.add(new PBoom(center));
        }

        // init shape coordinates
        heartx = new double[21];
        hearty = new double[21];
        double dmyt = 6.0 / 20.0;
        int index = 0;
        for (double myt = -3; myt < 3; myt += dmyt) {
            heartx[index] = 18 * Math.pow(Math.sin(myt), 3);
            hearty[index] = 14 * Math.cos(myt) - 5 * Math.cos(2 * myt) - 3 * Math.cos(3 * myt) - Math.cos(4 * myt);
//            System.out.println("debug: " + index + ", myt=" + myt  + ", dmyt=" + dmyt);
            index++;

        }

        timer = new AnimationTimer() {

            @Override
            public void handle(long l) {
                GraphicsContext gc = CANVAS.getGraphicsContext2D();
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                t1 = (t1 + dt1) % (2 * Math.PI);
                u1 = (u1 + du1) % (2 * Math.PI);
                pso = (pso + psdo1) % (2 * Math.PI);

                for (int ii = 0; ii < numberOfParticles; ii++) {
                    PBoom p = boom.get(ii);
                    p.display(gc);
                    p.update();
                    if ((p.radius < 0.4) || (!p.checkEdges())) {
                        t2 = (t2 + dt2) % (2 * Math.PI);
                        p.loc = new PVector(a1 + a2 * Math.cos(t1 + t2), b1 + b2 * Math.sin(t1 + t2 + u1));
                        double angle = Math.random() * psa + pso;
                        p.vel = new PVector(Math.sin(angle), Math.cos(angle));
                        p.vel.mult(psv * Math.random() + Math.random());
                        p.radius = psc + pss * Math.random();
                        pso = (pso + psdo2) % (2 * Math.PI);
                    }
                }

                if (idletimeout > 0) {
                    idletimeout--;
                    for (int i = 0; i < params.length; i++) {
                        gc.setFill(Color.WHITE);
                        Text txt = new Text(params[i]);
                        txt.setFont(new Font("Arial", 22));
                        double width = LEFTTAB - 5 - txt.getLayoutBounds().getWidth();
                        double y = 20 + i * 25;
                        gc.setFont(new Font("Arial", 22));
                        gc.fillText(params[i], width, y + 18);
                        gc.setLineWidth(2);
                        gc.setStroke(Color.WHITE);
                        gc.strokeRect(rectangles[i].getX(),
                                rectangles[i].getY(),
                                rectangles[i].getWidth(),
                                rectangles[i].getHeight());
                        switch (i) {
                            case 0:
                                gc.setFill(Color.WHITE);
                                gc.fillText("" + numberOfParticles, RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + numberOfParticles / 10, y + 2, 16, 16);
                                break;
                            case 1:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(a1), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + na1, y + 2, 16, 16);
                                break;
                            case 2:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(b1), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + nb1, y + 2, 16, 16);
                                break;
                            case 3:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(a2), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + na2, y + 2, 16, 16);
                                break;
                            case 4:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(b2), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + nb2, y + 2, 16, 16);
                                break;
                            case 5:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(dt1), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + ndt1, y + 2, 16, 16);
                                break;
                            case 6:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(du1), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + ndu1, y + 2, 16, 16);
                                break;
                            case 7:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(dt2), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + ndt2, y + 2, 16, 16);
                                break;
                            case 8:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(pss), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npss, y + 2, 16, 16);
                                break;
                            case 9:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(psc), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npsc, y + 2, 16, 16);
                                break;
                            case 10:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(pbf), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npbf, y + 2, 16, 16);
                                break;
                            case 11:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(psa), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npsa, y + 2, 16, 16);
                                break;
                            case 12:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(psdo1), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npsdo1, y + 2, 16, 16);
                                break;
                            case 13:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(psdo2), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npsdo2, y + 2, 16, 16);
                                break;
                            case 14:
                                gc.setFill(Color.WHITE);
                                gc.fillText(format.format(psv), RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + npsv, y + 2, 16, 16);
                                break;
                            case 15:
                                gc.setFill(Color.WHITE);
                                gc.fillText("" + shape, RIGTHTAB, y + 18);
                                gc.setFill(Color.PINK);
                                gc.fillOval(LEFTTAB + nshape, y + 2, 16, 16);
                                break;
                        }
                    }
                }
//                gc.fillText("" + tx + ", " + ty, 60, 500); // debug only
            }
        };
        timer.start();
    }

    public double handleMouseClick(double currentN, double newn, boolean centerGlue) {
        double oldn = currentN;
        double ret = oldn;
        double dist = oldn - newn;
        double absdist = Math.abs(dist);
        double sign = (dist == 0.0) ? 0 : dist / absdist;
        if (absdist > 100) {
            ret -= sign * 100;
        } else if (absdist > 10) {
            ret -= sign * 10;
        } else if (absdist > 1) {
            ret -= sign;
        }
        if ((centerGlue)
                && ((oldn < 500.0) && (ret > 500.0))
                || ((oldn > 500.0) && (ret < 500.0))) {
            ret = 500.0;
        }
        if (ret < 10.0) {
            ret = 10.0;
        } else if (ret > 1000.0) {
            ret = 1000.0;
        }
        return ret;
    }

    public void updateTrigeometries() {
        numberOfParticles = (int) (nnumberOfParticles * 10);
        a1 = WIDTH * 0.5 + (WIDTH * 0.5 * na1 / 1000 - WIDTH * 0.25);
        b1 = HEIGHT * 0.5 + (HEIGHT * 0.5 * nb1 / 1000 - HEIGHT * 0.25);
        a2 = WIDTH * 0.8 * na2 / 1000.0;
        b2 = HEIGHT * 0.8 * nb2 / 1000.0;
        dt1 = ndt1 / 2000.0 - 0.25;
        du1 = ndu1 / 2000.0 - 0.25;
        dt2 = ndt2 / 2000.0 - 0.25;
        pss = npss / 5.0;
        psc = npsc / 5.0;
        psa = Math.PI * npsa / 500.0;
        psdo1 = npsdo1 / 10000.0;
        psdo2 = npsdo2 / 10000.0;
        psv = npsv / 50.0;
        pbf = 1.0 - npbf / 10000.0;
        shape = (int) (6 * nshape / 1000.0);
    }

    public static void main(String[] args) {
        launch();
    }

    /**
     * Explosion particles
     *
     */
    class PBoom {

        PVector loc;
        PVector vel;
        PVector acc;
        double radius = 5 + 60 * Math.random();
        double tick = Math.PI * 2 * Math.random();
        double rotate = 360 * Math.random();
        private static final int s = 30;
        private static final int w = 5;

        /**
         *
         * @param l start location
         */
        public PBoom(PVector l) {
            loc = new PVector(l.x, l.y);
            double angle = 2 * Math.PI * Math.random();
            vel = new PVector(Math.sin(angle), Math.cos(angle));
            vel.mult(7 * Math.random());
            acc = new PVector(0, 0.02);
        }

        void update() {
            loc.add(vel);
            radius *= Stjerner.pbf;
            vel.add(acc);

        }

        // returns false if drop is outside edges
        boolean checkEdges() {
            boolean ret = true;
            if ((loc.x > WIDTH) || (loc.x < 0) || (loc.y > HEIGHT) || (loc.y < 0)) {
                ret = false;
            }
            return ret;
        }

        void display(GraphicsContext gc) {

            switch (Stjerner.shape) {
                case 0: // circle
                    gc.setFill(Color.YELLOW);
                    gc.fillOval(loc.x - radius / 2, loc.y - radius / 2, radius, radius);
                    break;
                case 1:// cute aliens
                    double S = 5;
                    gc.save();
                    gc.translate(loc.x, loc.y);
//            gc.rotate(rotate);
                    gc.scale(radius / 20, radius / 20);
                    gc.setFill(Color.RED);
                    gc.fillRect(1 * S, 0, 8 * S, 1 * S);
                    gc.fillRect(0, 1 * S, 10 * S, 9 * S);
                    double xx = 3 * Math.sin(tick * 2) * S;
                    gc.fillRect(xx, 8 * S, 4 * S, 5 * S);
                    gc.fillRect(xx + 6 * S, 8 * S, 4 * S, 5 * S);
                    gc.setFill(Color.WHITE); // hej
                    gc.fillRect(2 * S, 2 * S, 2 * S, 4 * S);
                    gc.fillRect(6 * S, 2 * S, 2 * S, 4 * S);
                    gc.setFill(Color.BLACK);
                    gc.fillRect(2 * S, 4 * S, 1 * S, 2 * S);
                    gc.fillRect(6 * S, 4 * S, 1 * S, 2 * S);
                    gc.restore();
                    tick += 0.01;
                    break;
                case 2:// danish flags
                    gc.save();
                    gc.translate(loc.x, loc.y);
                    gc.rotate(rotate);
                    gc.scale(radius / 20, radius / 20);
                    gc.setFill(Color.WHITE);
                    gc.fillRect(-20.0, -20.0, 60.0, 40.0);
                    gc.setFill(Color.RED);
                    gc.fillRect(-20.0, -20.0, 17.0, 17.0);
                    gc.fillRect(-20.0, 3.0, 17.0, 17.0);
                    gc.fillRect(3.0, -20.0, 37.0, 17.0);
                    gc.fillRect(3.0, 3.0, 37.0, 17.0);
                    gc.restore();
                    break;
                case 3:// hearts
                    gc.setStroke(Color.RED);
                    gc.save();
                    gc.translate(loc.x, loc.y);
                    gc.rotate(rotate);
                    gc.scale(radius / 20, radius / 20);
                    gc.beginPath();
                    for (int i = 0; i < heartx.length; i++) {
                        gc.lineTo(heartx[i], hearty[i]);
                    }
                    gc.stroke();
                    gc.restore();
                    break;
                case 4:// stars
                    gc.setFill(Color.AQUAMARINE);
                    double x = loc.x + (radius * Math.sin(0));
                    double y = loc.y + (radius * Math.cos(0));
//            new PBadBoiPolyVector(x, y, Color.YELLOW, Color.RED, 8, 9);
                    double a = 8 * Math.PI / 7;
                    gc.beginPath();
                    gc.moveTo(x, y);
                    for (double i = 1; i < 7; i += 1.0) {
                        x = loc.x + (radius * Math.sin(a * i));
                        y = loc.y + (radius * Math.cos(a * i));
                        gc.lineTo(x, y);
                    }
                    gc.closePath();
                    gc.fill();
                    break;
                case 5://Text
                    gc.setFill(Color.GREEN);
                    gc.fillText("Hello", loc.x, loc.y);
                    break;
            }
        }
    }
}
