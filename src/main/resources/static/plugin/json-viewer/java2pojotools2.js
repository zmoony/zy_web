function trimStr(str) {
	return str.replace(/(^\s*)|(\s*$)/g, "");
}
function isArray(o) {
	return Object.prototype.toString.call(o) === '[object Array]';
}
function firstToUpperCase(str) {
	return str.substr(0, 1).toUpperCase() + str.substr(1);
}
function camelCase(input) {
	return input;
}
function camelCaseWithFirstCharUpper(input) {
	if (!input) {
		return ""
	};
	input = camelCase(input);
	return input[0].toUpperCase() + input.substr(1);
}
function isShortTime(str) {
	var a = str.match(/^(\d{1,2})(:)?(\d{1,2})\2(\d{1,2})$/);
	return a != null;
}
function strDateTime(str) {
	var r = str.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
	return r != null;
}
function strDateTime(str) {
	var reg = /^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2}) (\d{1,2}):(\d{1,2}):(\d{1,2})$/;
	var r = str.match(reg);
	return r != null;
}
function isDate(date) {
	var isDate = ((new Date(date) !== "Invalid Date" && !isNaN(new Date(date))) && isNaN(( + date)));
	return isDate;
}
function isInt(n) {
	return n % 1 === 0;
}
function currentYear() {
	return new Date().getFullYear()
}
function currentTime() {
	var date = new Date();
//	var seperator1 = "-";
//	var seperator2 = ":";
	var seperator1 = "年";
	var seperator2 = "月";
	var month = date.getMonth() + 1;
	var strDate = date.getDate();
	if (month >= 1 && month <= 9) {
		month = "0" + month;
	}
	if (strDate >= 0 && strDate <= 9) {
		strDate = "0" + strDate;
	}
	var currentdate = date.getFullYear() + seperator1 + month + seperator1 + strDate + "日"; // + " " + date.getHours() + seperator2 + date.getMinutes() + seperator2 + date.getSeconds();
	return currentdate;
}
var importMap = {
	'Date': 'java.util.Date',
	'LocalDateTime': 'java.time.LocalDateTime',
	'List': 'java.util.List'
}
function toBeanText(bean, packageName) {
	var beanFields = bean.val;
	var className = bean.name;
	var importText = "";
	var fieldText = "";
	var setterText = "";
	var typeSet = {};
	var shoudImportJackson = false;
	var tpl = "    public void setA(T a) {\n \
        this.a = a;\n \
    }\n \
    public T getA() {\n \
        return a;\n \
    }\n\n";
	for (key in beanFields) {
		var camelKey = camelCase(key);
		if (camelKey != key) {
			fieldText += '    @JsonProperty("' + key + '")\n';
			shoudImportJackson = true;
		}
		
		var beanType = beanFields[key];
		beanType = beanType.replace("<int>","<Integer>").replace("<long>","<Long>").replace("<double>","<Double>");
		beanType = beanType.replace("<","&lt;").replace(">","&gt;");
		fieldText += "    /** 属性"+ camelKey +"*/\n    private " + beanType + " " + camelKey + ";\n\n";
		var type = beanFields[key];
		if (type.indexOf("List<") > -1) {
			type = beanFields[key].replace('List<', "");
			type = type.replace('>', "");
			typeSet["List"] = 'true';
		}
		typeSet[type] = 'true';
		var tplMap = {
			a: camelKey,
			A: firstToUpperCase(camelKey),
			T: beanFields[key]
		};
		setterText += tpl.replace(/a|A|T/g,
		function(matched) {
			return tplMap[matched];
		});
	}
	for (t in typeSet) {
		if (importMap[t]) {
			importText += "import " + importMap[t] + ";\n";
		}
	}
	if (shoudImportJackson) {
		importText += "import org.codehaus.jackson.annotate.JsonIgnoreProperties;\nimport org.codehaus.jackson.annotate.JsonProperty;"
	}
	
	importText = importText + "import lombok.Data;\n";
	if (packageName) {
		importText = "package " + packageName + ";\n\n" + importText;
	}
	
	var desc = "\n/**\n * @Title " + className + "\n * @Describe [功能描述]\n * @Author yuez\n * @Date " + currentTime() + "\n */\n@Data\n";
 
    //setterText 是get\set方法
	return importText + desc + "public class " + className + "\n{\n" + fieldText + "}";
}

function getBeanFieldFromJson(text, className, type, typeCase) {
	var jsonObject = null;
	text = trimStr(text);
	try {
		jsonlint.parse(text);
	} catch(e) {}
	if (text[0] === "[" && text[text.length - 1] === "]") {
		text = '{ "list": ' + text + '}';
		jsonObject = JSON.parse(text).list[0];
	} else {
		jsonObject = JSON.parse(text);
	}
	var bean = {};
	var attrClassAry = [];
	for (key in jsonObject) {
		var val = jsonObject[key];
		var newKey = key;
		switch (type) 
		{
			case '1':
				newKey = lineToHump(key);
				break;
			case '2':
				newKey = humpToLine(key);
				if (typeCase) 
				{
					if (typeCase === '2') 
					{
						newKey = newKey.toUpperCase()
					} else if (typeCase === '1') 
					{
						newKey = newKey.toLowerCase()
					}
				}
				break;
			default:
				break;
		}
		bean[newKey] = getTypeFromJsonVal(val, newKey, attrClassAry, type, typeCase);
	}
	if (!className) {
		className = "AtoolBean";
	} else {}
	bean = {
		name: className,
		val: bean
	};
	return $.merge([bean], attrClassAry);
}
function lineToHump(name) {
	var html = name.replace(/\_(\w)/g,
	function(all, letter) {
		return letter.toUpperCase();
	});
	return html
}
function humpToLine(name) {
	return name.replace(/([A-Z])/g, "_$1").toLowerCase();
}
function getTypeFromJsonVal(val, key, attrClassAry, type, typeCase) {
	if (val && val.replace) {
		val = trimStr(val);
	}
	var typeofStr = typeof(val);
	if (typeofStr === 'number') {
		if (isInt(val)) {
			return val < 65536 ? "int": "long";
		} else {
			return "double";
		}
	} else if (typeofStr === 'boolean') {
		return typeofStr;
	} else if (isDate(val)) {
		return "LocalDateTime";
	} else if (!val) {
		return "String";
	} else if (typeofStr === 'string') {
		return "String";
	} else {
		if (isArray(val)) {
			var type = getTypeFromJsonVal(val[0], key, attrClassAry);
			if (type == "int") {
				type = "Integer";
			} else if (type == "long") {
				type = "Long";
			} else if (type == "float") {
				type = "Float";
			} else if (type == "double") {
				type = "Double";
			}
			return "List<" + type + ">";
		} else {
			var typeName = camelCaseWithFirstCharUpper(key);
			var bean = {};
			for (key in val) {
				var fieldValue = val[key];
				var newKey = key;
				switch (type) {
				case '1':
					newKey = lineToHump(key);
					break;
				case '2':
					newKey = humpToLine(key);
					if (typeCase) {
						if (typeCase === '2') {
							newKey = newKey.toUpperCase()
						} else if (typeCase === '1') {
							newKey = newKey.toLowerCase()
						}
					}
					break;
				default:
					break;
				}
				bean[newKey] = getTypeFromJsonVal(fieldValue, newKey, attrClassAry, type, typeCase);
			}
			attrClassAry.push({
				name: typeName,
				val: bean
			});
			return typeName;
		}
	}
}