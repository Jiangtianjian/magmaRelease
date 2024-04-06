package magma.util.train_server;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public interface ITrainServer {
    void connect();

    void disconnect();

    void moveBall(Vector3D vector3D);

    void dropBall();

    void setPlayMode(String str);

    void freeKick(boolean z);

    void directFreeKick(boolean z);

    void killServer();

    void moveAgent(Vector3D vector3D, boolean z, int i);

    void resetTime();

    void requestFullState();
}