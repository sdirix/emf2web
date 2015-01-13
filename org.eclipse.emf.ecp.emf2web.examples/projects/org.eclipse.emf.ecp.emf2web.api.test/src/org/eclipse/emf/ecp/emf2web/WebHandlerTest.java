/*******************************************************************************
 * Copyright (c) 2014-2015 EclipseSource Muenchen GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Stefan Dirix - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.emf.ecp.emf2web;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecp.makeithappen.model.task.Gender;
import org.eclipse.emf.ecp.makeithappen.model.task.Nationality;
import org.eclipse.emf.ecp.makeithappen.model.task.TaskFactory;
import org.eclipse.emf.ecp.makeithappen.model.task.TaskPackage;
import org.eclipse.emf.ecp.makeithappen.model.task.User;
import org.junit.Test;

public class WebHandlerTest {

	@Test
	public void test() throws Exception {
		final WebHandler webHandler = new WebHandler("http://localhost:9000");

		List<EObject> elements = webHandler
			.getWebElements(TaskPackage.eINSTANCE.getUser());

		final int initialNewUserCount = getFirstNameCount(elements, "New User");
		final int initialEditedUserCount = getFirstNameCount(elements, "Edited User");

		final User newUser = TaskFactory.eINSTANCE.createUser();
		newUser.setActive(false);
		newUser.setDateOfBirth(DatatypeFactory.newInstance()
			.newXMLGregorianCalendar());
		newUser.setEmail("user@localhost");
		newUser.setFirstName("New User");
		newUser.setLastName("Last");
		newUser.setGender(Gender.MALE);
		newUser.setHeigth(100);
		newUser.setNationality(Nationality.ITALIAN);
		newUser.setTimeOfRegistration(new Date());
		newUser.setWeight(87.2);

		webHandler.createWebElement(newUser);

		elements = webHandler.getWebElements(TaskPackage.eINSTANCE.getUser());

		int currentNewUserCount = getFirstNameCount(elements, "New User");

		if (currentNewUserCount - 1 > initialNewUserCount) {
			fail("Created too many new users");
		} else if (currentNewUserCount - 1 < initialNewUserCount) {
			fail("Did not create new user");
		}

		newUser.setFirstName("Edited User");
		webHandler.updateWebElement(newUser);

		elements = webHandler.getWebElements(TaskPackage.eINSTANCE.getUser());

		currentNewUserCount = getFirstNameCount(elements, "New User");
		final int currentEditedUserCount = getFirstNameCount(elements, "Edited User");

		if (currentNewUserCount != initialNewUserCount) {
			fail("Failed to update the new user");
		} else if (currentEditedUserCount - 1 != initialEditedUserCount) {
			fail("Updated the new user wrongly");
		}
	}

	private int getFirstNameCount(List<EObject> elements, String firstName) {
		int result = 0;
		for (final EObject element : elements) {
			final User user = (User) element;
			if (user.getFirstName().equals(firstName)) {
				result++;
			}
		}
		return result;
	}
}
