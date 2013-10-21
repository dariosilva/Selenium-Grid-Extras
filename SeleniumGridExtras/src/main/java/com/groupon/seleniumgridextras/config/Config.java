package com.groupon.seleniumgridextras.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.StringMap;

import com.groupon.seleniumgridextras.config.driver.IEDriver;
import com.groupon.seleniumgridextras.config.driver.WebDriver;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

  public static final String ACTIVATE_MODULES = "active_modules";
  public static final String DISABLED_MODULES = "disabled_modules";
  public static final String SETUP = "setup";
  public static final String TEAR_DOWN = "tear_down";
  public static final String GRID = "grid";
  public static final String WEBDRIVER = "webdriver";
  public static final String IEDRIVER = "iedriver";
  public static final String EXPOSE_DIRECTORY = "expose_directory";

  public static final String AUTO_START_NODE = "auto_start_node";
  public static final String AUTO_START_HUB = "auto_start_hub";
  public static final String DEFAULT_ROLE = "default_role";
  public static final String NODE_CONFIG = "node_config";
  public static final String HUB_CONFIG = "hub_config";



  protected Map theConfigMap;

  public Config() {
    theConfigMap = new HashMap();
    initialize();
  }

  public Config(Boolean emptyConfig){
    theConfigMap = new HashMap();
    if (!emptyConfig){
      initialize();
    }

  }

  private void initialize(){
    getConfigMap().put(ACTIVATE_MODULES, new ArrayList<String>());
    getConfigMap().put(DISABLED_MODULES, new ArrayList<String>());
    getConfigMap().put(SETUP, new ArrayList<String>());
    getConfigMap().put(TEAR_DOWN, new ArrayList<String>());

    getConfigMap().put(GRID, new StringMap());
    initializeWebdriver();
    getConfigMap().put(IEDRIVER, new IEDriver());

    initializeHubConfig();
    initializeNodeConfig();
  }

  private void initializeNodeConfig() {
    getConfigMap().put(NODE_CONFIG, new NodeConfig());
  }

  private void initializeHubConfig() {
    getConfigMap().put(HUB_CONFIG, new Hub());
  }

  private void initializeWebdriver() {
    getConfigMap().put(WEBDRIVER, new WebDriver());
  }

  private Map getConfigMap() {
    return theConfigMap;
  }



  public static Config initilizedFromUserInput(){
    Config config = new Config(true);
    config.initializeWebdriver();
    config.initializeHubConfig();
    config.initializeNodeConfig();

    return FirstTimeRunConfig.customiseConfig(config);
  }

  public void overwriteConfig(Map overwrites){
    if (overwrites.containsKey("theConfigMap")){
      System.out.println("Merging config overwrites from file");
      System.out.println("File config" +  (Map<String, Object>) overwrites.get("theConfigMap"));
      System.out.println("Before\n\n\n" + getConfigMap());
      HashMapMerger.overwriteMergeStrategy(getConfigMap(), (Map<String, Object>) overwrites.get("theConfigMap"));
      System.out.println("After\n\n\n" + getConfigMap());
    }
  }


  public List<String> getActivatedModules() {
    return (List<String>) getConfigMap().get(ACTIVATE_MODULES);
  }

  public List<String> getDisabledModules() {
    return (List<String>) getConfigMap().get(DISABLED_MODULES);
  }

  public String getExposedDirectory() {
    return (String) getConfigMap().get(EXPOSE_DIRECTORY);
  }

  public List<String> getSetup() {
    return (List<String>) getConfigMap().get(SETUP);
  }

  public List<String> getTeardown() {
    return (List<String>) getConfigMap().get(TEAR_DOWN);
  }

  public StringMap getGrid() {
    return (StringMap) getConfigMap().get(GRID);
  }

  public void setIEdriver() {
//    this.put(IEDRIVERz)
  }

  public IEDriver getIEdriver() {
    try {
      return (IEDriver) getConfigMap().get(IEDRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(IEDRIVER);
      IEDriver ieDriver = new IEDriver();

      ieDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(IEDRIVER, ieDriver);

      return ieDriver;
    }
  }

  public WebDriver getWebdriver() {
    try {
      return (WebDriver) getConfigMap().get(WEBDRIVER);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(WEBDRIVER);
      WebDriver webDriver = new WebDriver();

      webDriver.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(WEBDRIVER, webDriver);

      return webDriver;
    }
  }


  public void writeToDisk(String file) {
    try {
      File f = new File(file);
      String config = this.toPrettyJsonString();
      FileUtils.writeStringToFile(f, config);
    } catch (Exception error) {
      System.out
          .println("Could not write default config file, exit with error " + error.toString());
      System.exit(1);
    }
  }

  public void addSetupTask(String task) {
    getSetup().add(task);
  }

  public void addTeardownTask(String task) {
    getTeardown().add(task);
  }

  public void addActivatedModules(String module) {
    getActivatedModules().add(module);
  }

  public void addDisabledModule(String module) {
    getDisabledModules().add(module);
  }

  public void setSharedDir(String sharedDir) {
    getConfigMap().put(EXPOSE_DIRECTORY, sharedDir);
  }

  public String toJsonString() {
    return new Gson().toJson(this);
  }

  public String toPrettyJsonString() {
    return new GsonBuilder().setPrettyPrinting().create().toJson(this);
  }

  public boolean checkIfModuleEnabled(String module) {
    return getActivatedModules().contains(module);
  }


  public JsonObject asJsonObject() {
    return (JsonObject) new JsonParser().parse(this.toJsonString());
  }


  public void setDefaultRole(String defaultRole) {
    getConfigMap().put(DEFAULT_ROLE, defaultRole);
  }

  public void setAutoStartHub(String autoStartHub) {
    getConfigMap().put(AUTO_START_HUB, autoStartHub);
  }

  public void setAutoStartNode(String autoStartNode) {
    getConfigMap().put(AUTO_START_NODE, autoStartNode);
  }

  public boolean getAutoStartNode() {
    return getConfigMap().get(AUTO_START_NODE).equals("1") ? true : false;
  }

  public boolean getAutoStartHub() {
    return getConfigMap().get(AUTO_START_HUB).equals("1") ? true : false;
  }

  public Hub getHub() {

    try {
      return (Hub) getConfigMap().get(HUB_CONFIG);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(HUB_CONFIG);
      Hub hubConfig = new Hub();

      hubConfig.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(HUB_CONFIG, hubConfig);

      return hubConfig;
    }
  }

  public void setHub(Hub hub) {
    getConfigMap().put(HUB_CONFIG, hub);
  }


  public NodeConfig getNode() {
    try {
      return (NodeConfig) getConfigMap().get(NODE_CONFIG);
    } catch (ClassCastException e) {
      StringMap
          stringMapFromGoogleWhoCantUseHashMapOnNestedObjects =
          (StringMap) getConfigMap().get(NODE_CONFIG);
      NodeConfig nodeConfig = new NodeConfig();

      nodeConfig.putAll(stringMapFromGoogleWhoCantUseHashMapOnNestedObjects);

      getConfigMap().put(NODE_CONFIG, nodeConfig);

      return nodeConfig;
    }

  }

  public String getDefaultRole() {
    return (String) getConfigMap().get(DEFAULT_ROLE);
  }
}
