package com.bmc.gibraltar.automation.tests.businesslogic.ui.process.designer;

import com.bmc.gibraltar.automation.dataprovider.RestDataProvider;
import com.bmc.gibraltar.automation.framework.autodb.annotation.GUID;
import com.bmc.gibraltar.automation.framework.autodb.annotation.Groups;
import com.bmc.gibraltar.automation.items.datadictionary.DataDictionary;
import com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup;
import com.bmc.gibraltar.automation.items.element.ActiveElement;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionEditorPage;
import com.bmc.gibraltar.automation.pages.ProcessDefinitionsTabPage;
import com.bmc.gibraltar.automation.tests.TaskManagerBaseTest;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

import java.util.ListIterator;

import static com.bmc.gibraltar.automation.items.datadictionary.DictionaryGroup.PROCESS_VARIABLES;
import static com.bmc.gibraltar.automation.items.element.ElementOfDesigner.*;
import static com.bmc.gibraltar.automation.items.element.ElementProperties.*;
import static com.bmc.gibraltar.automation.items.parameter.DataType.RECORD_INSTANCE;
import static com.bmc.gibraltar.automation.items.tab.InspectorGroup.INPUT_PARAMETERS;
import static java.util.Arrays.asList;

public class AssociationExpressionsTest extends TaskManagerBaseTest {
    private ProcessDefinitionsTabPage tabPage;
    private ProcessDefinitionEditorPage process;
    // Data
    private String recordDefinitionA = "A";
    private String recordDefinitionB = "B";
    private String recordDefinitionC = "C";
    private String inputParameterA = recordDefinitionA.toLowerCase();
    private String inputParameterB = recordDefinitionB.toLowerCase();
    private String inputParameterC = recordDefinitionC.toLowerCase();
    private String processName = "AssociationExpressionInDictionary" + RandomStringUtils.randomAlphanumeric(3);
    // Data

    //TODO: Create method deleteAssociation and getAssociationNames in RestDataProvider
    private void associationTestsDataPreparation(String... names) {
        RestDataProvider rest = new RestDataProvider("Demo", "");
        asList(names).stream().filter(r -> !rest.getRecordDefinitionNames().contains(r))
                .forEach(n -> rest.createRecordDefinition(n, "records/AssociationRecordDefinition.json"));
        ListIterator<String> iterator = asList(names).listIterator();
        iterator.forEachRemaining(a -> {
            if (iterator.hasNext()) {
                rest.createAssociation(a, iterator.next());
                iterator.previous();
            }
        });
    }

    private ActiveElement preliminaryPreparation() {
        logOut();
        loginToTaskManager("Demo", "");
        tabPage = new ProcessDefinitionsTabPage(wd).navigateToPage();
        process = tabPage.initiateNewProcess().setProcessName(processName);
        process.addProcessParam(INPUT_PARAMETERS,
                process.createParam(inputParameterA, RECORD_INSTANCE).setRecordDefinition(recordDefinitionA));
        process.addProcessParam(INPUT_PARAMETERS,
                process.createParam(inputParameterB, RECORD_INSTANCE).setRecordDefinition(recordDefinitionB));
        process.addProcessParam(INPUT_PARAMETERS,
                process.createParam(inputParameterC, RECORD_INSTANCE).setRecordDefinition(recordDefinitionC));
        ActiveElement sendMessage = process.dragAndDropToCanvas(SEND_MESSAGE);
        process.bindElements(process.getDroppedElement(START), sendMessage, process.getDroppedElement(END));
        return sendMessage;
    }

    //TODO: All tests are covers "type": "COMPOSITION" only. But it needs to run every test with "type": "AGGREGATION".

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyShowingAssociationsInDictionary")
    @Features("P537 - Association Expression")
    @Stories("US207276")
    @GUID("872e1949-feb2-4267-958e-bbd1b2f14a8a")
    public void verifyShowingAssociationsInDictionary() {
        String expectedExpressionInElementProperty = "${process.a}";
        String associationForInputParameterA = "a -> B -> C";
        String associationForInputParameterB = "b -> A, C";
        String associationForInputParameterC = "c -> B -> A";
        associationTestsDataPreparation(recordDefinitionA, recordDefinitionB, recordDefinitionC);
        ActiveElement el = preliminaryPreparation();
        DataDictionary dictionary = el.openDataDictionaryFor(BODY);
        process.verifyEquals(dictionary.getAllVars(PROCESS_VARIABLES.getName()),
                asList(inputParameterA, inputParameterB, inputParameterC),
                "PROCESS_VARIABLES group does not contain all input params");
        dictionary.open().doubleClickVar(DictionaryGroup.PROCESS_VARIABLES, inputParameterA);
        dictionary.verifyExpressionCorrectness(inputParameterA);
        dictionary.clickOk();
        dictionary.verifyResultCorrectness(expectedExpressionInElementProperty);
        verifyEquals(dictionary.open().getAssociationFor(inputParameterA), associationForInputParameterA);
        dictionary.close();
        verifyEquals(dictionary.open().getAssociationFor(inputParameterB), associationForInputParameterB);
        dictionary.close();
        verifyEquals(dictionary.open().getAssociationFor(inputParameterC), associationForInputParameterC);
    }

    @Test(groups = Groups.CATEGORY_FULL, description = "verifyAssociationExpressionInDictionary")
    @Features("P537 - Association Expression")
    @Stories("US207276")
    @GUID("d72f4ee6-dd1f-41ce-bad5-b3df4dac4264")
    public void verifyAssociationExpressionInDictionary() {
        String expectedExpressionForInputParameterA = "${process.a._associations.AToB.B} ${process.a._associations.BToC.C}";
        String expectedExpressionForInputParameterB = "${process.b._associations.AToB.A} ${process.b._associations.BToC.C}";
        String expectedExpressionForInputParameterC = "${process.c._associations.BToC.B} ${process.c._associations.AToB.A}";
        ActiveElement element = preliminaryPreparation();
        DataDictionary dictionary = element.openDataDictionaryFor(BODY);
        dictionary.open().addAllAssociationVarsFor(inputParameterA).apply();
        dictionary.verifyResultCorrectness(expectedExpressionForInputParameterA);
        dictionary.open().addAllAssociationVarsFor(inputParameterB).apply();
        dictionary.verifyResultCorrectness(expectedExpressionForInputParameterB);
        dictionary.open().addAllAssociationVarsFor(inputParameterC).apply();
        dictionary.verifyResultCorrectness(expectedExpressionForInputParameterC);
        element.setPropertyByDefault(SUBJECT).setPropertyByDefault(RECIPIENTS);
        process.saveProcess().closeProcess();
        tabPage.verifyProcessExists(processName);
    }
}
