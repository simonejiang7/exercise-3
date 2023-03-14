package farm;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class FarmKG extends Artifact {

    private static final String USERNAME = "danai";
    private static final String PASSWORD = "was";

    private String repoLocation;

    private static String PREFIXES = "PREFIX was: <https://was-course.interactions.ics.unisg.ch/farm-ontology#>\n"+
    "PREFIX hmas: <https://purl.org/hmas/>\n" +
    "PREFIX td: <https://www.w3.org/2019/wot/td#>\n";
    

    public void init(String repoLocation) {
        this.repoLocation = repoLocation;
    }

    @OPERATION
    public void queryFarm(OpFeedbackParam<String> farm){
        // the variable where we will store the result to be returned to the agent
        String farmValue = null;

        // sets your variable name for the farm to be queried
        String farmVariableName = "farm";

        // constructs query
        String queryStr = PREFIXES + "SELECT ?farm WHERE { ?" + farmVariableName + " a was:Farm. }";

        /* Example SPARQL query 
         * PREFIX was: <https://was-course.interactions.ics.unisg.ch/farm-ontology#>
         * PREFIX hmas: <https://purl.org/hmas/>
         * PREFIX td: <https://www.w3.org/2019/wot/td#>
         * SELECT ?farm WHERE { ?farm a was:Farm. }
         */

        // executes query
        JsonArray farmBindings = executeQuery(queryStr);
        
        /* Example JSON result
         * [{"farm":
         *  {
         *   "type":"uri",
         *   "value":"https://sandbox-graphdb.interactions.ics.unisg.ch/was-exercise-3-danai#farm-17c04810-567a-4236-b310-611bb4fd2a8c"
         *  }
         * }]
         */

        // handles result as JSON object
        JsonObject firstBinding = farmBindings.get(0).getAsJsonObject();
        JsonObject farmBinding = firstBinding.getAsJsonObject(farmVariableName);
        farmValue = farmBinding.getAsJsonPrimitive("value").getAsString();

        // sets the value of interest to the OpFeedbackParam
        farm.set(farmValue);
    }


    @OPERATION 
    public void queryThing(String farm, String offeredAffordance, OpFeedbackParam<String> thingDescription) {
        // the variable where we will store the result to be returned to the agent
        String tdValue = null; 

        // sets your variable name for the farm to be queried
        String tdVariableName = "td";

        // constructs query
        String queryStr = PREFIXES + "SELECT ?" + tdVariableName + " WHERE {\n" + 
            "<" + farm + "> hmas:contains ?thing.\n" +
            "?thing td:hasActionAffordance ?aff.\n" +
            "?thing hmas:hasProfile ?" + tdVariableName + ".\n" +
            "?aff a <" + offeredAffordance +">.} LIMIT 1";
        
        /* Example SPARQL query 
         * PREFIX was: <https://was-course.interactions.ics.unisg.ch/farm-ontology#>
         * PREFIX hmas: <https://purl.org/hmas/>
         * PREFIX td: <https://www.w3.org/2019/wot/td#>
         * SELECT ?td WHERE {
         *   <https://sandbox-graphdb.interactions.ics.unisg.ch/was-exercise-3-danai#farm-17c04810-567a-4236-b310-611bb4fd2a8c> hmas:contains ?thing.
         *   ?thing td:hasActionAffordance ?aff.
         *   ?thing hmas:hasProfile ?td.
         *   ?aff a <https://was-course.interactions.ics.unisg.ch/farm-ontology#ReadSoilMoistureAffordance>.
         * } LIMIT 1
         */

        // executes query
        JsonArray tdBindings = executeQuery(queryStr);

        /* Example JSON result
         * [{"td":
         *  {
         *   "type":"uri",
         *   "value":"https://raw.githubusercontent.com/Interactions-HSG/example-tds/was/tds/tractor1.ttl"
         *  }
         * }]
         */
        
        // handles result as JSON object
        JsonObject firstBinding = tdBindings.get(0).getAsJsonObject();
        JsonObject tdBinding = firstBinding.getAsJsonObject(tdVariableName);
        tdValue = tdBinding.getAsJsonPrimitive("value").getAsString();

        // sets the value of interest to the OpFeedbackParam
        thingDescription.set(tdValue);
    }

    @OPERATION
    public void queryFarmSections(String farm, OpFeedbackParam<Object> sections) {

        // the variable where we will store the result to be returned to the agent
        Object sectionValue = null; 

        // sets your variable name for the farm to be queried
        String sectionName = "section";

        // constructs query
        String queryStr = PREFIXES + "SELECT ?" + sectionName + " WHERE {\n" + 
            "<" + farm + "> a was:Farm.\n" +
            "?farm was:hasLandSection ?" + sectionName + ".\n" +
            "?" + sectionName +" a was:Section.";

        // executes query
        JsonArray sectionBindings = executeQuery(queryStr);

        // handles result as JSON object
        JsonObject firstBinding = sectionBindings.get(0).getAsJsonObject();
        JsonObject sectionBinding = firstBinding.getAsJsonObject(sectionName);
        sectionValue = sectionBinding.getAsJsonPrimitive("value");

        // sets the value of interest to the OpFeedbackParam
        sections.set(sectionValue);
    }

    @OPERATION
    public void querySectionCoordinates(String section, OpFeedbackParam<Object> coordinates) {
        // the variable where we will store the result to be returned to the agent
        // Object[] coordinatesValue = new Object[]{ 0, 0, 1, 1 };

        // sets the value of interest to the OpFeedbackParam

        // Object[] coordinatesValue = new Object[]{ 0, 0, 1, 1 };
        Object coordinateValue = null; 

        // sets your variable name for the farm to be queried
        String coordinatesValueName = "coordinates";

        // constructs query
        String queryStr = PREFIXES + "SELECT ?" + coordinatesValueName + " WHERE {\n" + 
            "<" + section + "> a was:Section.\n" +
            "?section was:hasCoordinates ?" + coordinatesValueName + ".\n" +
            "?" + coordinatesValueName +" a was:Coordinate.";

        // executes query
        JsonArray coordinateBindings = executeQuery(queryStr);

        // handles result as JSON object
        JsonObject firstBinding = coordinateBindings.get(0).getAsJsonObject();
        JsonObject sectionBinding = firstBinding.getAsJsonObject(coordinatesValueName);
        coordinateValue = sectionBinding.getAsJsonPrimitive("value");

        // sets the value of interest to the OpFeedbackParam
        coordinates.set(coordinateValue);
    }

    @OPERATION 
    public void queryCropOfSection(String section, OpFeedbackParam<String> crop) {
        // the variable where we will store the result to be returned to the agent
        // String cropValue = "fakeCrop";

        // // sets the value of interest to the OpFeedbackParam
        // crop.set(cropValue);

        String cropValue = null; 

        // sets your variable name for the farm to be queried
        String cropValueName = "coordinates";

        // constructs query
        String queryStr = PREFIXES + "SELECT ?" +  cropValueName + " WHERE {\n" + 
            "<" + section + "> a was:Section.\n" +
            "?section was:grows ?" +  cropValueName+ ".\n" +
            "?" + cropValueName +" a was:Crop.";

        // executes query
        JsonArray cropBindings = executeQuery(queryStr);

        // handles result as JSON object
        JsonObject firstBinding = cropBindings.get(0).getAsJsonObject();
        JsonObject sectionBinding = firstBinding.getAsJsonObject(cropValueName);
        cropValue = sectionBinding.getAsJsonPrimitive("value").getAsString();

        // sets the value of interest to the OpFeedbackParam
        crop.set(cropValue);
    }

    @OPERATION
    public void queryRequiredMoisture(String crop, OpFeedbackParam<Integer> level) {
        // the variable where we will store the result to be returned to the agent
        // Integer moistureLevelValue = 120;

        // // sets the value of interest to the OpFeedbackParam
        // level.set(moistureLevelValue);
        
        Integer moistureLevelValue = 0;

        // sets your variable name for the farm to be queried
        String moistureName = "soilmoisture";

        // constructs query
        String queryStr = PREFIXES + "SELECT ?" +  moistureName + " WHERE {\n" + 
            "<" + crop + "> a was:Crop.\n" +
            "?crop was:hasMoistureLevel ?" +  moistureName + ".\n" +
            "?" + moistureName +" a was:SoilMoisture.";

        // executes query
        JsonArray moistureBindings = executeQuery(queryStr);

        // handles result as JSON object
        JsonObject firstBinding = moistureBindings.get(0).getAsJsonObject();
        JsonObject moistureBinding = firstBinding.getAsJsonObject(moistureName);
        moistureLevelValue  = moistureBinding.getAsJsonPrimitive("value").getAsInt();

        // sets the value of interest to the OpFeedbackParam
        level.set(moistureLevelValue );
    }

    private JsonArray executeQuery(String queryStr) {
        String queryUrl = this.repoLocation + "?query=" +  URLEncoder.encode(queryStr, StandardCharsets.UTF_8);
        
        try {
            URI uri = new URI(queryUrl);
            String authString = USERNAME + ":" + PASSWORD;
            byte[] authBytes = authString.getBytes(StandardCharsets.UTF_8);
            String encodedAuth = Base64.getEncoder().encodeToString(authBytes);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Authorization", "Basic " + encodedAuth)
                .header("Accept", "application/sparql-results+json")
                .GET()
                .build();
             
            HttpClient client = HttpClient.newHttpClient();
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("HTTP error code : " + response.statusCode());
                }
                String responseBody = response.body();
                JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                JsonArray bindingsArray = jsonObject.getAsJsonObject("results").getAsJsonArray("bindings");
                return bindingsArray;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return new JsonArray();
    }
}
