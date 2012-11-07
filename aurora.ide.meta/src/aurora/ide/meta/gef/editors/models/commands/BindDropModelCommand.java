package aurora.ide.meta.gef.editors.models.commands;

import java.util.List;

import uncertain.composite.CompositeMap;
import aurora.ide.meta.gef.Util;
import aurora.ide.meta.gef.editors.EditorMode;
import aurora.ide.meta.gef.editors.models.AuroraComponent;
import aurora.ide.meta.gef.editors.models.BOX;
import aurora.ide.meta.gef.editors.models.CheckBox;
import aurora.ide.meta.gef.editors.models.Container;
import aurora.ide.meta.gef.editors.models.Dataset;
import aurora.ide.meta.gef.editors.models.GridColumn;
import aurora.ide.meta.gef.editors.models.Input;
import aurora.ide.meta.gef.editors.models.Label;
import aurora.ide.meta.gef.editors.models.ResultDataSet;
import aurora.ide.meta.gef.editors.models.ViewDiagram;

public class BindDropModelCommand extends DropBMCommand {

	private Container container;
	private Object data;

	private EditorMode editorMode;

	public EditorMode getEditorMode() {
		return editorMode;
	}

	public void setEditorMode(EditorMode editorMode) {
		this.editorMode = editorMode;
	}

	public void execute() {
		@SuppressWarnings("unchecked")
		List<CompositeMap> fields = (List<CompositeMap>) data;
		// fieldset
		if (container instanceof BOX || container instanceof ViewDiagram) {
			fillForm(fields);
		}
		if (container instanceof GridColumn) {
			fillGrid(fields);
		}
	}

	private boolean isQueryNameMap(CompositeMap f) {
		return "query-field".equals(f.getName());
	}

	private boolean isRefFieldMap(CompositeMap f) {
		return "ref-field".equals(f.getName());
	}

	private void fillGrid(List<CompositeMap> fields) {
		Dataset ds = Util.findDataset(container);
		String model = ds == null ? null : ds.getModel();
		for (CompositeMap f : fields) {
			if (isQueryNameMap(f))
				continue;
			if (model == null || "".equals(model.trim())) {
				if (ds != null)
					ds.setModel(f.getString("model", ""));
			}
			String string = Util.getPrompt(f,"");
			GridColumn gc = new GridColumn();
			gc.setPrompt(string);
			String name = f.getString("name");
			name = name == null ? "" : name;
			gc.setName(name);
			if (this.getEditorMode().isForCreate()
					|| this.getEditorMode().isForUpdate()) {
				if (isRefFieldMap(f))
					continue;
				String type = Util.getType(f);
				gc.setEditor(type);
			}
			container.addChild(gc);
		}
	}

	private void fillForm(List<CompositeMap> fields) {
		Dataset ds = Util.findDataset(container);
		String model = ds == null ? null : ds.getModel();
		for (CompositeMap field : fields) {
			if (isQueryNameMap(field)) {
				if (ds instanceof ResultDataSet) {
					continue;
				}
			}
			if (model == null || "".equals(model.trim())) {
				if (ds != null)
					ds.setModel(field.getString("model", ""));
			}
			String name = (String) field.get("field");
			name = name == null ? field.getString("name") : name;
			name = name == null ? "" : name;
			AuroraComponent input = this.getEditorMode().isForDisplay() ? new Label()
					: new Input();
			String type = this.getEditorMode().isForDisplay() ? Label.Label
					: Util.getType(field);
			if (CheckBox.CHECKBOX.equals(type)) {
				input = new CheckBox();
			}
			input.setType(type);
			input.setName(name);
			input.setPrompt(Util.getPrompt(field,""));
			container.addChild(input);
		}
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

}
