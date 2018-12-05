package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class Main extends Application {

    @FXML
    private Button cpuButton;

    @FXML
    private LineChart<Number, Number> romLineChart;
//    private LineChart<Number, Number> romLineChartArray[] = new LineChart[]{null};

    @FXML
    private LineChart<Number, Number> cpuLineChart;
//    private LineChart<Number, Number> cpuLineChartArray = new LineChart[]{null};

    @FXML
    private Button romButton;

    private static final int MAX_DATA_POINTS = 50;
    private int xSeriesData = 0;
    private XYChart.Series<Number, Number> cpuSeries = new XYChart.Series<>();
    private XYChart.Series<Number, Number> romSeries = new XYChart.Series<>();

    private XYChart.Series<Number, Number> cpuSeries1 = new XYChart.Series<Number, Number>();
    private XYChart.Series<Number, Number> romSeries1 = new XYChart.Series<Number, Number>();

    private ExecutorService executor;
    private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Number> dataQ2 = new ConcurrentLinkedQueue<>();
    private static Sigar sigar = new Sigar();


    private NumberAxis CPUxAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    private NumberAxis ROMxAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);

    public Main() throws SigarException {
    }

//    @FXML
    public void init(Stage stage) {

//        CPUxAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
        CPUxAxis.setForceZeroInRange(false);
        CPUxAxis.setAutoRanging(false);
        CPUxAxis.setTickLabelsVisible(false);
        CPUxAxis.setTickMarkVisible(false);
        CPUxAxis.setMinorTickVisible(false);

//        ROMxAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
        ROMxAxis.setForceZeroInRange(false);
        ROMxAxis.setAutoRanging(false);
        ROMxAxis.setTickLabelsVisible(false);
        ROMxAxis.setTickMarkVisible(false);
        ROMxAxis.setMinorTickVisible(false);

        NumberAxis CPUyAxis = new NumberAxis();
        CPUyAxis.setUpperBound(100);
        CPUyAxis.setAutoRanging(false);

        NumberAxis ROMyAxis = new NumberAxis();
        ROMyAxis.setUpperBound(100);
        ROMyAxis.setAutoRanging(false);

//         Create a LineChart
        cpuLineChart = new LineChart<Number, Number>(CPUxAxis, CPUyAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };


        romLineChart = new LineChart<Number, Number>(ROMxAxis, ROMyAxis) {
            // Override to remove symbols on each data point
            @Override
            protected void dataItemAdded(Series<Number, Number> series, int itemIndex, Data<Number, Number> item) {
            }
        };
        romLineChart.setVisible(false);

        cpuLineChart.setAnimated(false);
        cpuLineChart.setTitle("Cpu");
        cpuLineChart.setHorizontalGridLinesVisible(true);

        romLineChart.setAnimated(false);
        romLineChart.setTitle("Rom");
        romLineChart.setHorizontalGridLinesVisible(true);

        // Set Name for Series
        cpuSeries.setName("Series 1");
        romSeries.setName("Series 2");

        // Add Chart Series
        cpuLineChart.getData().addAll(cpuSeries);
        romLineChart.getData().addAll(romSeries);

        Pane pane = new FlowPane();
        pane.getChildren().addAll(cpuLineChart, romLineChart);
        stage.setScene(new Scene(pane));


//        primaryStage.setScene(new Scene(romLineChart));
    }


    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Animated Line Chart Sample");
        init(stage);

//        Parent root = FXMLLoader.load(getClass().getResource("/sample.fxml"));
//        stage.setTitle("Hello World");
//        stage.setScene(new Scene(root, 800, 500));

//        stage.setScene(new Scene(romLineChart));

        stage.show();
//        initialize();

        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });

        AddToQueue addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }

    private class AddToQueue implements Runnable {
        public void run() {
            try {
                // add a item of random data to queue
                CpuPerc cpuPerc = sigar.getCpuPerc();
                Mem mem = sigar.getMem();
                dataQ1.add((1 - cpuPerc.getIdle()) * 100);
//                dataQ1.add(Math.random());
                dataQ2.add(mem.getUsedPercent());

                Thread.sleep(1000);
                executor.execute(this);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (SigarException e) {
                e.printStackTrace();
            }
        }
    }

    //-- Timeline gets called in the JavaFX Main thread
    private void prepareTimeline() {
        // Every frame to take any data from queue and add to chart
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    addDataToSeries();
                } catch (SigarException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void addDataToSeries() throws SigarException, InterruptedException {
        ObservableList<XYChart.Data<Number, Number>> cpuList = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> romList = FXCollections.observableArrayList();
        for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
            if (dataQ1.isEmpty()) break;
//            cpuList.add(new XYChart.Data<Number, Number>(xSeriesData++, dataQ1.remove()));
//            romList.add(new XYChart.Data<Number, Number>(xSeriesData++, dataQ2.remove()));
//            cpuSeries1.setData(cpuList);
//            romSeries1.setData(romList);
            cpuSeries.getData().add(new XYChart.Data<>(xSeriesData++, dataQ1.remove()));
            romSeries.getData().add(new XYChart.Data<>(xSeriesData, dataQ2.remove()));
        }
        // remove points to keep us at no more than MAX_DATA_POINTS
        if (cpuSeries.getData().size() > MAX_DATA_POINTS) {
            cpuSeries.getData().remove(0, cpuSeries.getData().size() - MAX_DATA_POINTS);
        }
        if (romSeries.getData().size() > MAX_DATA_POINTS) {
            romSeries.getData().remove(0, romSeries.getData().size() - MAX_DATA_POINTS);
        }
        // update
        CPUxAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        CPUxAxis.setUpperBound(xSeriesData - 1);

        ROMxAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        ROMxAxis.setUpperBound(xSeriesData - 1);
    }

    public static void main(String[] args) throws SigarException {
        launch(args);
//        new Main().initialize();
    }

    @FXML
    void onCpuButton(ActionEvent event) {
        cpuLineChart.setVisible(true);
        romLineChart.setVisible(false);
    }

    @FXML
    void onRomButton(ActionEvent event) {
        cpuLineChart.setVisible(false);
        romLineChart.setVisible(true);
    }
}