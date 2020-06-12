package com.michaelwasher.bricker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.michaelwasher.bricker.R;
import com.michaelwasher.bricker.Resources.V2;

public class Platform extends View {

    //    private float left, right, top, bottom;
    protected V2 a, b, c, d;          // The corners
    protected V2 pab, pbd, pdc, pca;  // Perpendiculars to the sides

    protected float boarderWidth = 5;
    Context context;
    // TODO Rewrite this whole class to use real names Only 4 valeus are need, top left bottom right
    // Not drawing misc shapes
    ///////////////////// PRIVATE HELPER METHODS ////////////////////////

    // Calculate reflection on one edge
    protected boolean reflectLine(V2 centre, V2 direction, float radius, V2 p, V2 q, V2 ppq) {
        if (!inside(centre, radius, p, q, ppq)) {
            float divisor, gamma, lambda;
            V2 pp, qq;
            pp = V2.add(p, V2.multiply(ppq, radius));
            qq = V2.add(q, V2.multiply(ppq, radius));
            divisor = direction.y * (pp.x - qq.x) - direction.x * (pp.y - qq.y);
            if (divisor != 0) {
                gamma = (direction.y * (pp.x - centre.x) - direction.x * (pp.y - centre.y)) / divisor;
                if (gamma >= 0 && gamma <= 1) {
                    lambda = ((pp.y - centre.y) * (pp.x - qq.x) - (pp.x- centre.x) * (pp.y - qq.y)) / divisor;
                    if (lambda > 0 && lambda <= 1) {
                        //centre.x += lambda * direction.x;
                        //centre.y += lambda * direction.y;
                        // Update direction in place
                        V2 newdir = V2.subtract(direction, V2.multiply(V2.multiply(ppq, V2.dot(direction, ppq)), 2));
                        direction.x = newdir.x;
                        direction.y = newdir.y;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Calculate reflection on a curved corner
    protected boolean reflectCurve(V2 centre, V2 direction, float radius, V2 p, V2 q, V2 r, V2 ppq, V2 prp) {
        if (V2.subtract(centre, p).length() >= radius) {
            // The ball is outside the circle(radius) about p
            float deltax, deltay, lensq, bb, cc, det, lambda;
            deltax = p.x - centre.x;
            deltay = p.y - centre.y;
            lensq = (direction.x * direction.x + direction.y * direction.y);
            bb = (direction.x * deltax + direction.y * deltay) / lensq;
            cc = (deltax * deltax + deltay * deltay - radius * radius) / lensq;
            det = bb * bb - cc;
            if (det >= 0) {
                lambda = bb - (float) Math.sqrt(det);
                if (lambda >= 0 && lambda <= 1) {
                    V2 hit = V2.add(centre, V2.multiply(direction, lambda));
                    if (!inside(hit, 0, p, q, ppq) && !inside(hit, 0, r, p, prp)) {
                        V2 normal = V2.subtract(hit, p).normalize();
                        V2 newdir = V2.subtract(direction, V2.multiply(V2.multiply(normal, V2.dot(direction, normal)), 2));
                        direction.x = newdir.x;
                        direction.y = newdir.y;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Is a ball of radius r 'inside' with respect to one edge
    protected boolean inside(V2 x, float r, V2 p, V2 q, V2 ppq) {
        return V2.dot(ppq, V2.subtract(x, V2.add(p, V2.multiply(ppq, r)))) < 0;
    }

    ////////////////// PUBLIC METHODS ////////////////////////////////////

    // Constructors
    public Platform(Context context) {
        this(context, null);
    }
    public Platform(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d("Platform Created", "A platform has been created.");
        //Wrapper Defintion
        V2 corner = new V2(this.getX(), this.getY());
        float width = this.getWidth();
        float height = this.getHeight();

        a = corner;
        b = V2.add(a, new V2(width, 0));
        c = V2.add(a, new V2(0, height));
        d = V2.add(a, new V2(width, height));
        pab = V2.subtract(a, c).normalize();
        pbd = V2.subtract(b, a).normalize();
        pdc = pab.negate();
        pca = pbd.negate();
        this.context = context;
    }

    // Reflection of a ball against the rectangle - updates 'direction'
    public void reflectBall(V2 centre, V2 direction, float radius) {
        if (reflectLine(centre, direction, radius, a, b, pab)) return;
        if (reflectLine(centre, direction, radius, b, d, pbd)) return;
        if (reflectLine(centre, direction, radius, d, c, pdc)) return;
        if (reflectLine(centre, direction, radius, c, a, pca)) return;
        if (reflectCurve(centre, direction, radius, b, d, a, pbd, pab)) return;
        if (reflectCurve(centre, direction, radius, d, c, b, pdc, pbd)) return;
        if (reflectCurve(centre, direction, radius, c, a, d, pdc, pca)) return;
        if (reflectCurve(centre, direction, radius, a, b, c, pab, pca)) return;
    }

    // Does a ball intersect with the rectangle
    public boolean intersectBall(V2 centre, float radius) {
        if (inside(centre, radius, a, b, pab)
                && inside(centre, radius, b, d, pbd)
                && inside(centre, radius, d, c, pdc)
                && inside(centre, radius, c, a, pca)) {
            if (!inside(centre, 0, a, b, pab)) {
                if (!inside(centre, 0, c, a, pca)) {
                    if (V2.subtract(centre, a).length() >= radius)
                        return false;
                } else if (!inside(centre, 0, b, d, pbd)) {
                    if (V2.subtract(centre, b).length() >= radius)
                        return false;
                }
            } else if (!inside(centre, 0, d, c, pdc)) {
                if (!inside(centre, 0, c, a, pca)) {
                    if (V2.subtract(centre, c).length() >= radius)
                        return false;
                } else if (!inside(centre, 0, b, d, pbd)) {
                    if (V2.subtract(centre, d).length() >= radius)
                        return false;
                }
            }
            return true;
        }
        return false;
    }
    //Getters and Setters
    public V2 getStartPointClone()
    {
        return new V2(a.x,a.y);
    }
    public V2 getStartPoint()
    {
        return a;
    }

    public void repositionRelative(int x, int y) {
        //Point A
        a.x += x;
        a.y += y;
        //Point B
        b.x += x;
        b.y += y;
        //Point C
        c.x += x;
        c.y += y;
        //Point D
        d.x += x;
        d.y += y;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //draw the View
        int width = this.getWidth();
        int height = this.getHeight();
        Drawable sprite = context.getDrawable(R.drawable.ic_platform);
        sprite.setBounds(0, 0,
                width, height);
        sprite.draw(canvas);
    }

}
