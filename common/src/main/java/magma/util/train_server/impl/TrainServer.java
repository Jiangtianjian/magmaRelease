package magma.util.train_server.impl;
import magma.util.train_server.ITrainServer;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.optimization.direct.CMAESOptimizer;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;


public class TrainServer implements ITrainServer {
    public Socket socket;
    public String serverHost;
    public int port;

    public PrintWriter out = null;

    public TrainServer(String host, int port) {
        this.serverHost = host;
        this.port = port;
    }

    @Override
    public void connect() {
        try {
            this.socket = new Socket(this.serverHost, this.port);
            this.out = new PrintWriter(this.socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void disconnect() {
        close();
    }

    @Override
    public void dropBall() {
        sendMessage("(dropBall)");
    }

    @Override
    public void setPlayMode(String mode) {
        sendMessage(String.format("(playMode %s)", mode));
    }

    @Override
    public void freeKick(boolean left) {
        setPlayMode(left ? "free_kick_left" : "free_kick_right");
    }

    @Override
    public void directFreeKick(boolean left) {
        setPlayMode(left ? "direct_free_kick_left" : "direct_free_kick_right");
    }

    @Override
    public void killServer() {
        sendMessage("(killsim)");
    }

    @Override
    public void moveAgent(Vector3D pos, boolean leftTeam, int agentID) {
        String team = leftTeam ? "Left" : "Right";
        String m = String.format(Locale.US, "(agent (team %s)(unum %d)(pos %.2f %.2f %.2f))", team, Integer.valueOf(agentID), Double.valueOf(pos.getX()), Double.valueOf(pos.getY()), Double.valueOf(pos.getZ()));
        sendMessage(m);
    }

    @Override
    public void resetTime() {
        sendMessage("(time 0)");
    }

    @Override
    public void requestFullState() {
        sendMessage("(reqfullstate)");
    }

    public void close() {
        if (this.socket != null) {
            try {
                this.socket.close();
                this.out.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        this.socket = null;
        this.out = null;
    }

    @Override
    public void moveBall(Vector3D pos) {
        moveBall(pos, new Vector3D(CMAESOptimizer.DEFAULT_STOPFITNESS, CMAESOptimizer.DEFAULT_STOPFITNESS, CMAESOptimizer.DEFAULT_STOPFITNESS));
    }

    public void moveBall(Vector3D pos, Vector3D vel) {
        sendMessage(String.format(Locale.US, "(ball (pos %.2f %.2f %.2f) (vel %.2f %.2f %.2f))", Double.valueOf(pos.getX()), Double.valueOf(pos.getY()), Double.valueOf(pos.getZ()), Double.valueOf(vel.getX()), Double.valueOf(vel.getY()), Double.valueOf(vel.getZ())));
    }

    public void sendMessage(String msg) {
        if (this.out == null) {
            System.out.println("Cannot send message " + msg + " - not connected to server");
            return;
        }
        char[] buf = new char[4 + msg.length()];
        char[] msgSize = intToBytes(msg.length());
        System.arraycopy(msgSize, 0, buf, 0, 4);
        for (int i = 0; i < msg.length(); i++) {
            buf[i + 4] = msg.charAt(i);
        }
        this.out.write(buf);
        this.out.flush();
    }

    private char[] intToBytes(int i) {
        char[] buf = {(char) ((i >> 24) & 255), (char) ((i >> 16) & 255), (char) ((i >> 8) & 255), (char) (i & 255)};
        return buf;
    }
}