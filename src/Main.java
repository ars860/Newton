import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    Complex calcOneIteration(Complex x_n) {
        return x_n.minus((x_n.times(x_n).times(x_n).minus(new Complex(1, 0))).divides(x_n.times(x_n).times(new Complex(3, 0))));
    }

    List<Complex> calcNIterations(Complex start, int amount, int saveTail) {
        List<Complex> tail = new ArrayList<>();
        tail.add(start);

        Complex prev = start;
        for (int i = 0; i < amount; i++) {
            prev = calcOneIteration(prev);

            if (amount - i <= saveTail) {
                tail.add(prev);
            }
        }

        return tail;
    }

    private final double eps = 0.0000000001;
    private final Complex root1 = new Complex(1, 0);
    private final Complex root2 = new Complex(-0.5, -0.866025403784438646);
    private final Complex root3 = new Complex(-0.5, 0.866025403784438646);
    private int imageSize;
    BufferedImage image;
    Graphics2D graphics;

    public Main(int imageSize) {
        this.imageSize = imageSize;
        image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_RGB);
        graphics = image.createGraphics();
    }

    Complex findConvergence(List<Complex> tail) {
        double maxDifference = 1;
        for (final Complex a : tail) {
            for (final Complex b : tail) {
                if (a != b) {
                    maxDifference = Math.min(Complex.distance(a, b), maxDifference);
                }
            }
        }

        if (maxDifference < eps) {
            Complex last = tail.get(tail.size() - 1);

            if (Complex.distance(last, root1) < eps) {
                return root1;
            }
            if (Complex.distance(last, root2) < eps) {
                return root2;
            }
            return Complex.distance(last, root3) < eps ? root3 : null;
        }

        return null;
    }

    int fromDoubleToCoordinate(Double x) {
        return (int) ((x + 2) * imageSize / 4);
    }

    void drawPath(Complex x_start, int iterationsAmount) {
        List<Complex> iterations = calcNIterations(x_start, iterationsAmount, iterationsAmount);

        for (int i = 0; i < iterations.size() - 1; i++) {
            Complex begin = iterations.get(i);
            Complex end = iterations.get(i + 1);
            graphics.drawLine(
                    fromDoubleToCoordinate(begin.re()),
                    imageSize - 1 - fromDoubleToCoordinate(begin.im()),
                    fromDoubleToCoordinate(end.re()),
                    imageSize - 1 - fromDoubleToCoordinate(end.im()));
        }
    }

    public static void main(String[] args) throws IOException {
        Main kek = new Main(1000);

        for (int reIter = 0; reIter < kek.imageSize; reIter++) {
            System.out.println(reIter);
            for (int imIter = 0; imIter < kek.imageSize; imIter++) {
                double re = 4d / kek.imageSize * reIter - 2;
                double im = 4d / kek.imageSize * imIter - 2;

                Complex limit = kek.findConvergence(kek.calcNIterations(new Complex(re, im), 100, 50));

                if (limit == null) {
                    kek.image.setRGB(reIter, kek.imageSize - 1 - imIter, Color.BLACK.getRGB());
                }
                if (limit == kek.root1) {
                    kek.image.setRGB(reIter, kek.imageSize - 1 - imIter, Color.RED.getRGB());
                }
                if (limit == kek.root2) {
                    kek.image.setRGB(reIter, kek.imageSize - 1 - imIter, Color.GREEN.getRGB());
                }
                if (limit == kek.root3) {
                    kek.image.setRGB(reIter, kek.imageSize - 1 - imIter, Color.BLUE.getRGB());
                }
            }
        }

        kek.drawPath(new Complex(1, 1), 100);
        kek.drawPath(new Complex(0.1, 0.2), 100);
//        kek.drawPath(new Complex(-1, -0.5), 100);
//        kek.drawPath(new Complex(-0.1, -0.1), 100);

        File outputfile = new File("image.png");
        ImageIO.write(kek.image, "png", outputfile);
    }
}
