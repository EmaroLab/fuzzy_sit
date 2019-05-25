package it.emarolab.fuzzySIT.memoryLike.perception.simple2D;

import it.emarolab.fuzzySIT.memoryLike.perception.FeaturedSpatialObject;
import it.emarolab.fuzzySIT.memoryLike.perception.PerceptionBase;
import it.emarolab.fuzzySIT.semantic.axioms.SpatialRelation;

public class ConnectObjectScene extends PerceptionBase<Point2> {

    // the name of the types of objects in this example (π)
    public static final String LEG = "LEG";
    public static final String TABLE = "TABLE";
    public static final String CONNECTOR = "CONNECTOR";
    public static final String PEN = "PEN";
    public static final String CONTAINER = "CONTAINER";
    // the name of the spatial relations used in this example (ζ)
    public static final String CONNECTED = "isConnectedTo";

    private static final double CONNECTED_THRESHOLD = 0.15; // meters (positive number)

    // the name of individuals indicating objects in the scene
    public static final String LEG_IND_PREFIX = "indL";
    public static final String TABLE_IND_PREFIX = "indT";
    public static final String CONNECTOR_IND_PREFIX = "indC";
    public static final String PEN_IND_PREFIX = "indP";
    public static final String CONTAINER_IND_PREFIX = "indR";
    // functions to get sequential individual names
    private static int legCnt = 0, tableCnt = 0, connectorCnt = 0, penCnt = 0, containerCnt = 0;
    private static String getNewLegInd(){
        return LEG_IND_PREFIX + legCnt++;
    }
    private static String getNewTableInd(){
        return TABLE_IND_PREFIX + tableCnt++;
    }
    private static String getNewConnectorInd(){
        return CONNECTOR_IND_PREFIX + connectorCnt++;
    }
    private static String getNewPenInd(){
        return PEN_IND_PREFIX + penCnt++;
    }
    private static String getNewContainerInd(){
        return CONTAINER_IND_PREFIX + containerCnt++;
    }

    public ConnectObjectScene() {}
    public ConnectObjectScene(String sceneName) {
        super(sceneName);
    }

    @Override
    protected SpatialRelation computeRelation(FeaturedSpatialObject<Point2> anObject, FeaturedSpatialObject<Point2> newObject) {
        Point2 aFeature = anObject.getFeature();
        Point2 newFeature = newObject.getFeature();
        double connection = aFeature.distance( newFeature);
        if ( connection <= CONNECTED_THRESHOLD) {
            double degree = 1 - (Math.abs( connection) / CONNECTED_THRESHOLD);
            // [0.000000000000001,0.999999999999999] set with resolution ROLE_SHOULDER_RESOLUTION = "#.####"
            //if ( degree >= 0.000000000000001 & degree <= .999999999999999)
                return new SpatialRelation(anObject.getObject(), CONNECTED, newObject.getObject(), degree);
            //else System.err.println("Error on computing fuzzy degree: 1-" + connection + "/" + CONNECTED_THRESHOLD + "=" + degree);
        }
        return null;
    }

    public void addLeg( double xPose, double yPose, double degree){
        Point2 feature = new Point2(xPose,yPose);
        this.addObject( ConnectObjectScene.LEG, ConnectObjectScene.getNewLegInd(), degree, feature);
    }
    public void addTable( double xPose, double yPose, double degree){
        Point2 feature = new Point2(xPose,yPose);
        this.addObject( ConnectObjectScene.TABLE, ConnectObjectScene.getNewTableInd(), degree, feature);
    }
    public void addConnector( double xPose, double yPose, double degree){
        Point2 feature = new Point2(xPose,yPose);
        this.addObject( ConnectObjectScene.CONNECTOR, ConnectObjectScene.getNewConnectorInd(), degree, feature);
    }
    public void addContainer( double xPose, double yPose, double degree){
        Point2 feature = new Point2(xPose,yPose);
        this.addObject( ConnectObjectScene.CONTAINER, ConnectObjectScene.getNewContainerInd(), degree, feature);
    }
    public void addPen( double xPose, double yPose, double degree){
        Point2 feature = new Point2(xPose,yPose);
        this.addObject( ConnectObjectScene.PEN, ConnectObjectScene.getNewPenInd(), degree, feature);
    }

}
