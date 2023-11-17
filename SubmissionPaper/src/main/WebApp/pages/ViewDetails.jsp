<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.demo.Customers" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Customer Details</title>
   <!--  = <style>
     .popup-container {
            display: none;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            border: 1px solid #ccc;
            padding: 20px;
            background-color: black;
            z-index: 1000;
            
        }
    </style>
    <script>
    	function openUpdatePopUp(customerID)
    	{
    		var updatePath = "updateCustomers?customerID=" + customerID; //create the path
    		console.log("Hello World");
    		var popUpWindow = window.open(updatePath, "_blank", "width=700, height=700"); //popup window
    		
    		if(window.focus)
    		{
    			popUpWindow.focus();
    		}
    		return false;
    	}
    </script>-->
</head>
<body>
    <h1>Customer Details:</h1>

    <% 
        List<Customers> customersList = (List<Customers>) request.getAttribute("customers");

        if (customersList != null && !customersList.isEmpty()) {
            for (Customers customer : customersList) {
    %>
                <form action="deletedCustomers" method="post">
	                <h4>Customer id is: <%= customer.getCustID() %></h4>
	                <h4>Customer name is: <%= customer.getCustName() %></h4>
	                <h4>Customer email is: <%= customer.getCustEmail() %></h4>
	                <input type="hidden" name="customerID" value="<%= customer.getCustID() %>"/>
	                <button type="submit">Delete</button>
                </form>
               <!--   <button type="button" onclick="openUpdatePopUp('<%= customer.getCustID()%>')">Update</button>-->
                <!-- Form for Update -->
                <form action="updateCustomers" method="put">
                    <input type="hidden" name="customerID" value="<%= customer.getCustID() %>"/>
                    <label for="updatedName">Updated Name: </label>
                    <input type="text" name="updatedName" required/>
                    <label for="updatedEmail">Updated Email: </label>
                    <input type="email" name="updatedEmail" required/>
                    <button type="submit">Update</button>
                </form>
                <hr>
                
    <%
            }
        } else {
    %>
            <p>No customers available.</p>
    <%
        }
    %>
	<!-- Popup container for the update form 
    <div class="popup-container" id="updatePopup">
        <form action="updateCustomers" method="post">
            <input type="hidden" name="customerID" value="" id="updateCustomerID"/>
            <label for="updatedName">Updated Name: </label>
            <input type="text" name="updatedName" required/>
            <label for="updatedEmail">Updated Email: </label>
            <input type="email" name="updatedEmail" required/>
            <button type="submit">Update</button>
        </form>
    </div>-->
</body>
</html>
