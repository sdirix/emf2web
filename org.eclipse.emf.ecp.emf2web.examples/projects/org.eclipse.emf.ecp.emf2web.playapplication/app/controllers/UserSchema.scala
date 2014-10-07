package controllers

import org.qbproject.api.schema.QBSchema._
import org.qbproject.api.mongo.MongoSchemaExtensions._
import controllers.QBView._

object UserSchema {
		val modelSchema = qbClass(	
			"id" -> objectId,
			"firstName" -> qbString,
			"lastName" -> qbString,
			"gender" -> qbEnum("Male", "Female"),
			"active" -> qbBoolean,
			"timeOfRegistration" -> qbDateTime,
			"weight" -> qbNumber,
			"heigth" -> qbInteger,
			"nationality" -> qbEnum("German", "French", "UK", "US", "Spanish", "Italian", "Russian"),
			"dateOfBirth" -> qbDateTime,
			"email" -> qbString
		)
		val viewSchema = QBViewModel(
			modelSchema,
			QBHorizontalLayout(
				QBVerticalLayout(
					QBLabel("TestLabel"),
					QBViewControl("firstName", QBViewPath("firstName")),
					QBViewControl("lastName", QBViewPath("lastName")),
					QBViewControl("nationality", QBViewPath("nationality")),
					QBViewControl("gender", QBViewPath("gender")),
					QBViewControl("active", QBViewPath("active"))
				),
				QBVerticalLayout(
					QBViewControl("heigth", QBViewPath("heigth")),
					QBViewControl("timeOfRegistration", QBViewPath("timeOfRegistration")),
					QBViewControl("dateOfBirth", QBViewPath("dateOfBirth")),
					QBViewControl("email", QBViewPath("email")),
					QBViewControl("weight", QBViewPath("weight"))
				)
			)
		)
}
