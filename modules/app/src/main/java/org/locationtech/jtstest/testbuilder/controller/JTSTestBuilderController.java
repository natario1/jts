/*
 * Copyright (c) 2016 Vivid Solutions.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *
 * http://www.eclipse.org/org/documents/edl-v10.php.
 */

package org.locationtech.jtstest.testbuilder.controller;

import java.io.IOException;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jtstest.testbuilder.GeometryEditPanel;
import org.locationtech.jtstest.testbuilder.JTSTestBuilder;
import org.locationtech.jtstest.testbuilder.JTSTestBuilderFrame;
import org.locationtech.jtstest.testbuilder.JTSTestBuilderToolBar;
import org.locationtech.jtstest.testbuilder.model.DisplayParameters;
import org.locationtech.jtstest.testbuilder.model.GeometryEditModel;
import org.locationtech.jtstest.testbuilder.model.LayerList;
import org.locationtech.jtstest.testbuilder.model.TestBuilderModel;
import org.locationtech.jtstest.testbuilder.ui.SwingUtil;
import org.locationtech.jtstest.testbuilder.ui.tools.DeleteVertexTool;
import org.locationtech.jtstest.testbuilder.ui.tools.EditVertexTool;
import org.locationtech.jtstest.testbuilder.ui.tools.ExtractComponentTool;
import org.locationtech.jtstest.testbuilder.ui.tools.InfoTool;
import org.locationtech.jtstest.testbuilder.ui.tools.LineStringTool;
import org.locationtech.jtstest.testbuilder.ui.tools.PanTool;
import org.locationtech.jtstest.testbuilder.ui.tools.PointTool;
import org.locationtech.jtstest.testbuilder.ui.tools.RectangleTool;
import org.locationtech.jtstest.testbuilder.ui.tools.StreamPolygonTool;
import org.locationtech.jtstest.testbuilder.ui.tools.Tool;
import org.locationtech.jtstest.testbuilder.ui.tools.ZoomTool;
import org.locationtech.jtstest.util.io.MultiFormatReader;


public class JTSTestBuilderController 
{ 
  /*
  private static boolean autoZoomOnNextChange = false;

  
  public static void requestAutoZoom()
  {
    autoZoomOnNextChange  = true;
  }
  */
  public JTSTestBuilderController() {
    
  }

  public static TestBuilderModel model() {
    return frame().getModel();
  }
  public static GeometryEditPanel editPanel() {
    return JTSTestBuilderFrame.getGeometryEditPanel();
  }

  public static JTSTestBuilderToolBar toolbar() {
    return frame().getToolbar();
  }  

  public static JTSTestBuilderFrame frame() {
    return JTSTestBuilderFrame.instance();
  }
  
  public GeometryEditPanel getGeometryEditPanel()
  {
    return JTSTestBuilderFrame.getGeometryEditPanel();
  }

  public GeometryEditModel geomEditModel() {
    return JTSTestBuilder.model().getGeometryEditModel();
  }
  
  public void setFillType(int fillType) {
    DisplayParameters.setFillType(fillType);
    geometryViewChanged();
  }
  
  public void geometryViewChanged()
  {
    getGeometryEditPanel().updateView();
    //TODO: provide autoZoom checkbox on Edit tab to control autozooming (default = on)
  }
  
  public Geometry getGeometryA() {
    return geomEditModel().getGeometry(0);
  }

  public Geometry getGeometryB() {
    return geomEditModel().getGeometry(1);
  }

  public void exchangeGeometry() {
    geomEditModel().exchangeGeometry();
  }
  
  public void addTestCase(Geometry[] geom, String name)
  {
    model().addCase(geom, name);
    JTSTestBuilderFrame.instance().updateTestCases();
    JTSTestBuilderFrame.instance().showGeomsTab();
  }
  
  public void extractComponentsToTestCase(Coordinate pt)
  {
    double toleranceInModel = getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry comp = lyrList.getComponent(pt, toleranceInModel);
    if (comp == null) 
      return;
    model().addCase(new Geometry[] { comp, null });
    JTSTestBuilderFrame.instance().updateTestCases();
  }
  
  public void extractComponentsToTestCase(Geometry aoi, boolean isSegments)
  {
    //double toleranceInModel = JTSTestBuilderFrame.getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry[] comp;
    comp = lyrList.getComponents(aoi, isSegments);
    if (comp == null) 
      return;
    model().addCase(comp);
    JTSTestBuilderFrame.instance().updateTestCases();
    toolbar().selectZoomButton();
    modeZoomIn();
  }

  public void copyComponentToClipboard(Coordinate pt)
  {
    double toleranceInModel = getGeometryEditPanel().getToleranceInModel();
    LayerList lyrList = model().getLayers();
    Geometry comp = lyrList.getComponent(pt, toleranceInModel);
    if (comp == null) 
      return;
    SwingUtil.copyToClipboard(comp, false);
  }
  
  public void setFocusGeometry(int index) {
    model().getGeometryEditModel().setEditGeomIndex(index);
    toolbar().setFocusGeometry(index);    
  }

  public void inspectGeometry()
  {
    JTSTestBuilderFrame.instance().actionInspectGeometry();
  }

  public void inspectResult()
  {
    JTSTestBuilderFrame.instance().inspectResult();
  }

  public void inspectGeometryDialog()
  {
    JTSTestBuilderFrame.instance().actionInspectGeometryDialog();
  }
  public void clearResult()
  {
    frame().getResultWKTPanel().clearResult();
    model().setResult(null);
    editPanel().updateView();
  }
  
  public void setResult(Object result) {
    model().setResult(result);
    frame().getResultWKTPanel().setExecutedTime("");
    frame().getResultWKTPanel().setResult(result);
    geometryViewChanged();
  }
  
  public void setCommandErr(String msg) {
    frame().getCommandPanel().setError(msg);
  }
  
  public void saveImageAsPNG() {
    JTSTestBuilderFrame.instance().actionSaveImageAsPNG();
  }
  public void saveImageToClipboard() {
    JTSTestBuilderFrame.instance().actionSaveImageToClipboard();
  }
  
  public void updateLayerList() {
    JTSTestBuilderFrame.instance().updateLayerList();
  }
  
  //================================
      

  
  private void setTool(Tool tool) {
    editPanel().setCurrentTool(tool);
  }
  public void modeDrawRectangle() {
    setTool(RectangleTool.getInstance());
  }

  public void modeDrawPolygon() {
    setTool(StreamPolygonTool.getInstance());
  }

  public void modeDrawLineString() {
    setTool(LineStringTool.getInstance());
  }

  public void modeDrawPoint() {
    setTool(PointTool.getInstance());
  }

  public void modeInfo() {
    setTool(InfoTool.getInstance());
  }

  public void modeExtractComponent() {
    setTool(ExtractComponentTool.getInstance());
  }

  public void modeDeleteVertex() {
    setTool(DeleteVertexTool.getInstance());
  }
  public void modeEditVertex() {
    setTool(EditVertexTool.getInstance());
  }
  public void modeZoomIn() {
    setTool(ZoomTool.getInstance());
  }

  public void modePan() {
    setTool(PanTool.getInstance());
  }
  public void zoomOneToOne() {
    editPanel().getViewport().zoomToInitialExtent();
  }

  public void zoomToFullExtent() {
    editPanel().zoomToFullExtent();
  }

  public void zoomToResult() {
    editPanel().zoomToResult();
  }

  public void zoomToInput() {
    editPanel().zoomToInput();
  }

  public void zoomToInputA() {
    editPanel().zoomToGeometry(0);
  }

  public void zoomToInputB() {
    editPanel().zoomToGeometry(1);
  }
  
  public void caseMoveToPrev(boolean isZoom) {
    model().cases().prevCase();
    frame().updateTestCaseView();
    if (isZoom) zoomToInput();
  }

  public void caseMoveToNext(boolean isZoom) {
    model().cases().nextCase();
    frame().updateTestCaseView();
    if (isZoom) zoomToInput();
  }

  public void caseCopy() {
    model().cases().copyCase();
    frame().updateTestCases();
  }
  
  public void caseCreateNew() {
    model().cases().createNew();
    frame().showGeomsTab();
    frame().updateTestCases();
  }
  
  public void caseDelete() {
    model().cases().deleteCase();
    frame().updateTestCases();
  }
  
  //========================================
  
  public void displayInfo(Coordinate modelPt)
  {
    displayInfo( editPanel().getInfo(modelPt) );
  }
  
  public void displayInfo(String s)
  {
    displayInfo(s, true);
  }
  
  public void displayInfo(String s, boolean showTab)
  {
    frame().getLogPanel().addInfo(s);
    if (showTab) frame().showInfoTab();
  }
}
