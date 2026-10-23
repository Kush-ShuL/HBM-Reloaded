package com.hbm.client.railway.train;
/** Actual source occupied-slot thresholds and crate positions, not a replacement cargo mesh. */
final class RailTrainLoadLayout {static double[][] positions(int slots){
if(slots<=5)return new double[][]{{0.0D,0.375D,0.0D}};
if(slots<=10)return new double[][]{{0.1D,0.375D,0.25D},{-0.1D,0.375D,-0.25D}};
if(slots<=15)return new double[][]{{0.1D,0.375D,0.0D},{-0.1D,0.375D,0.375D},{-0.1D,0.375D,-0.375D}};
if(slots<=20)return new double[][]{{0.2D,0.375D,0.3D},{0.2D,0.375D,-0.2D},{-0.2D,0.375D,0.2D},{-0.2D,0.375D,-0.3D}};
if(slots<=25)return new double[][]{{0.2D,0.375D,0.6D},{0.2D,0.375D,0.0D},{0.2D,0.375D,-0.5D},{-0.2D,0.375D,0.2D},{-0.2D,0.375D,-0.3D}};
if(slots<=30)return new double[][]{{0.2D,0.375D,0.6D},{0.2D,0.375D,0.0D},{0.2D,0.375D,-0.5D},{-0.2D,0.375D,0.5D},{-0.2D,0.375D,-0.1D},{-0.2D,0.375D,-0.6D}};
if(slots<=35)return new double[][]{{0.2D,0.375D,0.4D},{0.2D,0.375D,0.0D},{0.2D,0.375D,-0.4D},{-0.2D,0.375D,0.3D},{-0.2D,0.375D,-0.1D},{-0.2D,0.375D,-0.5D},{0.0D,0.6875D,-0.25D}};
if(slots<=40)return new double[][]{{0.2D,0.375D,0.4D},{0.2D,0.375D,0.0D},{0.2D,0.375D,-0.4D},{-0.2D,0.375D,0.3D},{-0.2D,0.375D,-0.1D},{-0.2D,0.375D,-0.5D},{0.0D,0.6875D,-0.25D},{0.0D,0.6875D,0.15D}};
return new double[][]{{0.2D,0.375D,0.4D},{0.2D,0.375D,0.0D},{0.2D,0.375D,-0.4D},{-0.2D,0.375D,0.3D},{-0.2D,0.375D,-0.1D},{-0.2D,0.375D,-0.5D},{0.0D,0.6875D,-0.25D},{0.0D,0.6875D,0.15D},{-0.1D,0.375D,0.8D}};
}private RailTrainLoadLayout(){}}
