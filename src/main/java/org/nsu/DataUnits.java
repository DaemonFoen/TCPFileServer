package org.nsu;

import lombok.Getter;

@Getter
public enum DataUnits{
        MB(1024*1024),
        SEC(1000),
        TB(1024D*1024*1024*1024);

        private final double value;
        DataUnits(double value){
            this.value = value;
        }
        public double convert(double value) {
            return value/this.value;
        }
        public static double convertToMbSec(double totalBytes, double time){
            return  (DataUnits.MB.convert(totalBytes)) / (DataUnits.SEC.convert(time));
        }
}