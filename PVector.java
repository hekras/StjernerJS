/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author oldhandmixer
 * Vector class to help making the particles simpler
 *
 */
class PVector {

    double x;
    double y;

    PVector(double x_, double y_) {
        x = x_;
        y = y_;
    }

    void add(PVector v) {
        x += v.x;
        y += v.y;
    }

    static PVector add(PVector v, PVector u) {
        return (new PVector(v.x + u.x, v.y + u.y));
    }

    void sub(PVector v) {
        x -= v.x;
        y -= v.y;
    }

    static PVector sub(PVector v, PVector u) {
        return (new PVector(v.x - u.x, v.y - u.y));
    }

    static PVector random2D() {
        double angle = 2 * Math.PI * Math.random();
        return (new PVector(Math.cos(angle), Math.sin(angle)));
    }

    void mult(double n) {
        x *= n;
        y *= n;
    }

    void div(double n) {
        x /= n;
        y /= n;
    }

    double mag() {
        return (double) Math.sqrt(x * x + y * y);
    }

    double dist(PVector p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return (double) Math.sqrt(dx * dx + dy * dy);
    }

    void normalize() {
        double m = mag();
        if (m != 0) {
            div(m);
        }
    }

    void limit(double max) {
        if (mag() > max) {
            normalize();
            mult(max);
        }
    }
}
