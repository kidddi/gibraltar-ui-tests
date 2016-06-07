package com.bmc.gibraltar.automation.dataprovider;

import com.bmc.gibraltar.automation.items.element.ElementOfDesigner;
import com.bmc.gibraltar.automation.items.rule.RuleAction;
import com.bmc.gibraltar.automation.items.tab.InspectorGroup;
import org.testng.annotations.DataProvider;

import java.util.Arrays;

public class ElementsProperties {

    @DataProvider
    public static Object[][] elementsPropertiesMap() {
        Group geometryGroup = new Group(InspectorGroup.GEOMETRY, 4, new String[]{"Y:", "Height:", "X:", "Width:"});
        Group actionsPropertiesGroup = new Group(InspectorGroup.PROPERTIES, 4, new String[]{"Name:", "Label:", "Description:", "Run as:"});
        Group propertiesGroup = new Group(InspectorGroup.PROPERTIES, 2, new String[]{"Label:", "Description:"});
        Group multiInstanceGroup = new Group(InspectorGroup.MULTI_INSTANCE_LOOP, 1, new String[]{"Loop Type:"});

        return new Object[][]{

                new Object[]{ElementOfDesigner.START, 2,
                        Arrays.asList(propertiesGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.END, 2,
                        Arrays.asList(propertiesGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.EXCLUSIVE_GATEWAY, 2,
                        Arrays.asList(propertiesGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.PARALLEL_GATEWAY, 2,
                        Arrays.asList(propertiesGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.CREATE_RECORD_INSTANCE, 5,
                        Arrays.asList(actionsPropertiesGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.SUB_PROCESS, 3,
                        Arrays.asList(propertiesGroup, multiInstanceGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.UPDATE_RECORD_INSTANCE, 5,
                        Arrays.asList(actionsPropertiesGroup, geometryGroup)
                },

                new Object[]{ElementOfDesigner.USER_TASK, 2,
                        Arrays.asList(geometryGroup,
                                new Group(InspectorGroup.PROPERTIES, 3, new String[]{"Label:", "Description:",
                                        "Record Definition:"}))
                },

                new Object[]{ElementOfDesigner.CALL_ACTIVITY, 2,
                        Arrays.asList(geometryGroup,
                                new Group(InspectorGroup.PROPERTIES, 3, new String[]{"Label:", "Description:",
                                        "Called Process:"}))
                },

                new Object[]{ElementOfDesigner.CREATE_TASK, 5,
                        Arrays.asList(actionsPropertiesGroup, multiInstanceGroup, geometryGroup,
                                new Group(InspectorGroup.INPUT_MAP, 7, new String[]{"Submitter:", "Summary:",
                                        "Priority:", "Assigned To:", "Notes:", "Task Name:", "Status:"}))
                },

                new Object[]{ElementOfDesigner.SEND_MESSAGE, 5,
                        Arrays.asList(actionsPropertiesGroup, multiInstanceGroup, geometryGroup,
                                new Group(InspectorGroup.INPUT_MAP, 3, new String[]{"Subject:", "Body:", "Recipients:"}))
                },

                new Object[]{ElementOfDesigner.CREATE_BASECAMP_TO_DO, 5,
                        Arrays.asList(actionsPropertiesGroup, multiInstanceGroup, geometryGroup,
                                new Group(InspectorGroup.INPUT_MAP, 7, new String[]{"Project Id:",
                                        "Todo List Id:", "Assignee Id:", "Content:", "Due At:",
                                        "Connection Instance:", "Assignee Type:"}))
                },

                new Object[]{ElementOfDesigner.ANNOTATION, 2,
                        Arrays.asList(geometryGroup,
                                new Group(InspectorGroup.PROPERTIES, 1, new String[]{"Notes:"}))
                },
        };
    }

    @DataProvider
    public static Object[][] actionElementProperties() {
        Group propertiesGroup = new Group(InspectorGroup.PROPERTIES, 2, new String[]{"Label:", "Description:"});

        return new Object[][]{
                new Object[]{
                        RuleAction.CREATE_RECORD_INSTANCE, 2,
                        Arrays.asList(propertiesGroup,
                                new Group(InspectorGroup.INPUT_MAP, 1, new String[]{"Record Definition Name:"},
                                        new String[]{"Record Definition Name:", "Record Instance Id:"})
                        )
                },

                new Object[]{RuleAction.UPDATE_RECORD_INSTANCE, 2,
                        Arrays.asList(propertiesGroup,
                                new Group(InspectorGroup.INPUT_MAP, 2,
                                        new String[]{"Record Definition Name:", "Record Instance Id:"},
                                        new String[]{"Record Definition Name:", "Record Instance Id:"})
                        )
                },
                new Object[]{RuleAction.START_PROCESS, 1,
                        Arrays.asList(
                                new Group(InspectorGroup.PROPERTIES, 2, new String[]{"Label:", "Process To Start:"},
                                        new String[]{"Process To Start:"}))
                }
        };
    }

    public static class Group {
        private InspectorGroup inspectorGroup;
        private int labelsCount;
        private String[] labels;
        private String[] requiredLabels;

        public Group(InspectorGroup group, int labelsCount, String[] labels) {
            this.inspectorGroup = group;
            this.labels = labels;
            this.labelsCount = labelsCount;
        }

        public Group(InspectorGroup group, int labelsCount, String[] labels, String[] requiredLabels) {
            this.inspectorGroup = group;
            this.labels = labels;
            this.labelsCount = labelsCount;
            this.requiredLabels = requiredLabels;
        }

        public String getGroupName() {
            return inspectorGroup.getGroupName();
        }

        public InspectorGroup getGroup() {
            return inspectorGroup;
        }

        public int getLabelsCount() {
            return labelsCount;
        }

        public String[] getLabels() {
            return labels;
        }

        public String[] getRequiredLabels() {
            return requiredLabels == null ? new String[]{} : requiredLabels;
        }
    }
}