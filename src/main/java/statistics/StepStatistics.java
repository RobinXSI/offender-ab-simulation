package statistics;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;

public class StepStatistics {
    public SynchronizedSummaryStatistics walkDistance = new SynchronizedSummaryStatistics();
    public SynchronizedSummaryStatistics numberOfCrimes = new SynchronizedSummaryStatistics();
    public SynchronizedSummaryStatistics numberOfVenues = new SynchronizedSummaryStatistics();

    final int runNumber;
    final int totalDistance = 3;
    final double totalCrimes = 40707650.91602749456;

    public StepStatistics(int runNumber) {
        this.runNumber = runNumber;
    }

    @Override
    public String toString() {
        return "StepStatistics{" +
                ", walkDistanceMean=" + walkDistance.getMean() +
                ", walkDistanceTotal=" + walkDistance.getSum() +
                ", numberOfCrimesMean=" + numberOfCrimes.getMean() +
                ", numberOfCrimesTotal=" + numberOfCrimes.getSum() +
                ", numberOfVenuesMean=" + numberOfVenues.getMean() +
                ", numberOfVenuesTotal=" + numberOfVenues.getSum() +
                ", runNumber=" + runNumber +
                ", totalDistance=" + totalDistance +
                ", totalCrimes=" + totalCrimes +
                '}';
    }

    public static String csvHeader() {
        return "walkDistanceMean" +
                ",walkDistanceTotal" +
                ",numberOfCrimesMean" +
                ",numberOfCrimesTotal" +
                ",numberOfVenuesMean" +
                ",numberOfVenuesTotal" +
                ",runNumber" +
                ",totalDistance" +
                ",totalCrimes";
    }

    public String toCSV() {
        return walkDistance.getMean() +
                "," + walkDistance.getSum() +
                "," + numberOfCrimes.getMean() +
                "," + numberOfCrimes.getSum() +
                "," + numberOfVenues.getMean() +
                "," + numberOfVenues.getSum() +
                "," + runNumber +
                "," + totalDistance +
                "," + totalCrimes;
    }
}
