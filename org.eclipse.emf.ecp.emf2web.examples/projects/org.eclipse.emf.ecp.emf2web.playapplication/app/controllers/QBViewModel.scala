package controllers

import org.qbproject.api.schema.{QBJson, QBType}
import play.api.libs.json._

/**
 * Created by Edgar on 26.05.2014.
 */
object QBView {

  // TODO: or reference via val?
  case class QBViewPath(domainPath: String)

  trait QBViewElement

  case class QBViewControl(name: String, path: QBViewPath) extends QBViewElement

  case class QBLabel(text: String) extends QBViewElement

  case class QBGroup(name: String, elements: QBViewElement*) extends QBContainer

  trait QBContainer extends QBViewElement {
    def elements: Seq[QBViewElement]
  }

  trait QBViewLayout extends QBContainer

  case class QBVerticalLayout(elements: QBViewElement*) extends QBViewLayout

  case class QBHorizontalLayout(elements: QBViewElement*) extends QBViewLayout

  case class QBViewModel(domainType: QBType, elements: QBViewElement*)

  implicit def qbViewModelWriter: Writes[QBViewModel] = OWrites[QBViewModel] { viewModel =>
    Json.obj(
      // "domainType" -> "user", // TODO: how to reference
      "elements" -> viewModel.elements.map(qbViewElementWriter.writes)
    )
  }

  implicit def qbViewControlWriter: Writes[QBViewControl] = OWrites[QBViewControl] { viewControl =>
    Json.obj(
      "type" -> "Control",
      "path" -> viewControl.path.domainPath,
      "name" -> viewControl.name
    )
  }

  implicit def qbViewLabelWriter: Writes[QBLabel] = OWrites[QBLabel] { label =>
    Json.obj(
      "type" -> "Label",
      "text" -> label.text
    )
  }

  implicit def qbViewGroupWriter: Writes[QBGroup] = OWrites[QBGroup] { group =>
    Json.obj(
      "type" -> "Group",
      "name" -> group.name,
      "elements" -> group.elements.map(qbViewElementWriter.writes)
    )
  }

  implicit def qbViewLayoutWriter: Writes[QBViewLayout] = OWrites[QBViewLayout] { layout =>
    Json.obj(
      "type" -> layout.getClass.getSimpleName,
      "elements" -> layout.elements.map(qbViewElementWriter.writes)
    )
  }

  implicit def qbViewElementWriter: Writes[QBViewElement] = OWrites[QBViewElement] {
    case ctrl: QBViewControl => qbViewControlWriter.writes(ctrl).as[JsObject]
    case grp: QBGroup => qbViewGroupWriter.writes(grp).as[JsObject]
    case lbl: QBLabel => qbViewLabelWriter.writes(lbl).as[JsObject]
    case layout: QBViewLayout => qbViewLayoutWriter.writes(layout).as[JsObject]
  }
}