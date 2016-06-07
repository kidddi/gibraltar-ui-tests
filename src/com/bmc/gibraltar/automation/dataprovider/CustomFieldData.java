package com.bmc.gibraltar.automation.dataprovider;

import com.bmc.gibraltar.automation.items.parameter.DataType;
import com.bmc.gibraltar.automation.items.parameter.ProcessParameter;
import org.testng.annotations.DataProvider;

public class CustomFieldData {
    private static ProcessParameter personProcessParam =
            new ProcessParameter("Person", DataType.PERSON, true, "helpText", "");
    private static ProcessParameter recordProcessParam =
            new ProcessParameter("Record", DataType.RECORD_INSTANCE, true, "Task", false, "Helper");

    @DataProvider
    public static Object[][] customFields() {
        return new Object[][]{
                new Object[]{personProcessParam},
                new Object[]{recordProcessParam},
        };
    }

    @DataProvider
    public static Object[][] customOptions() {
        return new Object[][]{
                new Object[]{
                        personProcessParam,
                        new CustomField("birthDate", "Birth Date", "${process.%s.birthDate}")
                },

                new Object[]{
                        personProcessParam,
                        new CustomField("name", "Name", "${process.%s.name}")
                },

                new Object[]{
                        recordProcessParam,
                        //TODO: get ID of the field using rest data provider, instead of hardcoding its ID
                        new CustomField("assignedTo", "Assigned To", "${process.%s.4}"),
                },

                new Object[]{
                        recordProcessParam,
                        //TODO: get ID of the field using rest data provider, instead of hardcoding its ID
                        new CustomField("summary", "Summary", "${process.%s.8}")
                }
        };
    }

    public static class CustomField {
        private String property;
        private String propertyNameInDictionary;
        private String expectedExpression;

        public CustomField(String property, String propertyNameInDictionary, String expectedExpression) {
            this.property = property;
            this.propertyNameInDictionary = propertyNameInDictionary;
            this.expectedExpression = expectedExpression;
        }

        public String getProperty() {
            return property;
        }

        public String getPropertyNameInDictionary() {
            return propertyNameInDictionary;
        }

        public String getExpectedExpression() {
            return expectedExpression;
        }
    }
}
