const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell,
        AlignmentType, BorderStyle, WidthType, ShadingType, HeadingLevel,
        LevelFormat, Header, Footer, PageNumber } = require('docx');
const fs = require('fs');

// 创建文档
const doc = new Document({
  styles: {
    default: { document: { run: { font: "Microsoft YaHei", size: 22 } } },
    paragraphStyles: [
      { id: "Heading1", name: "Heading 1", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 28, bold: true, font: "Microsoft YaHei", color: "1a1a1a" },
        paragraph: { spacing: { before: 240, after: 120 }, outlineLevel: 0 } },
      { id: "Heading2", name: "Heading 2", basedOn: "Normal", next: "Normal", quickFormat: true,
        run: { size: 24, bold: true, font: "Microsoft YaHei", color: "333333" },
        paragraph: { spacing: { before: 180, after: 80 }, outlineLevel: 1 } },
      { id: "Normal", name: "Normal", 
        run: { font: "Microsoft YaHei", size: 22 },
        paragraph: { spacing: { after: 60 } } }
    ]
  },
  numbering: {
    config: [
      { reference: "bullets",
        levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } } }] },
      { reference: "bullets2",
        levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } } }] },
      { reference: "skills-bullets",
        levels: [{ level: 0, format: LevelFormat.BULLET, text: "•", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 480, hanging: 280 } } } }] }
    ]
  },
  sections: [{
    properties: {
      page: {
        margin: { top: 1008, right: 1080, bottom: 1008, left: 1080 }
      }
    },
    children: [
      // ========== 姓名 + 联系方式 ==========
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { after: 80 },
        children: [new TextRun({ text: "[你的姓名]", bold: true, size: 36, font: "Microsoft YaHei" })]
      }),
      new Paragraph({
        alignment: AlignmentType.CENTER,
        spacing: { after: 200 },
        children: [
          new TextRun({ text: "求职意向：Agent工程师 / AI应用开发工程师", size: 22, color: "555555" })
        ]
      }),

      // 联系信息表格
      new Table({
        width: { size: 8500, type: WidthType.DXA },
        columnWidths: [2125, 2125, 2125, 2125],
        rows: [new TableRow({
          children: [
            createInfoCell("📧 [你的邮箱]"),
            createInfoCell("📱 [你的手机]"),
            createInfoCell("📍 [所在城市]"),
            createInfoCell("💻 GitHub: [链接]")
          ]
        })]
      }),
      
      new Paragraph({ spacing: { after: 120 }, children: [] }),

      // ========== 教育背景 ==========
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "2E75B6", space: 1 } },
        children: [new TextRun("教育背景")]
      }),
      new Paragraph({ spacing: { after: 60 }, children: [] }),
      
      new Table({
        width: { size: 8500, type: WidthType.DXA },
        columnWidths: [5000, 3500],
        rows: [new TableRow({
          children: [
            new TableCell({
              borders: noBorder(), width: { size: 5000, type: WidthType.DXA },
              children: [new Paragraph({ children: [new TextRun({ text: "[大学名称]", bold: true, size: 23 })] })]
            }),
            new TableCell({
              borders: noBorder(), width: { size: 3500, type: WidthType.DXA },
              children: [new Paragraph({ alignment: AlignmentType.RIGHT, children: [new TextRun({ text: "[入学时间] - [毕业时间]", color: "666666" })] })]
            })
          ]
        }), new TableRow({
          children: [
            new TableCell({
              borders: noBorder(), width: { size: 5000, type: WidthType.DXA }, columnSpan: 2,
              children: [new Paragraph({ children: [new TextRun({ text: "[专业名称] | 本科", color: "555555" })] })]
            })
          ]
        })]
      }),

      new Paragraph({ spacing: { after: 80 }, children: [] }),

      // ========== 专业技能 ==========
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "2E75B6", space: 1 } },
        children: [new TextRun("专业技能")]
      }),
      new Paragraph({ spacing: { after: 40 }, children: [] }),

      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("编程语言与框架")]
      }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("前端：Vue.js / JavaScript / TypeScript / HTML5 / CSS3")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("后端：Java / Spring Boot / MyBatis-Plus / MySQL")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("工具：Git / Maven / Node.js / Webpack")] }),

      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("AI/Agent 领域")]
      }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("熟悉 Python 基础，了解 AI/LLM 相关生态")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("了解 LangChain、Agent 开发框架及 RAG 技术原理")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("熟悉 OpenAI API / 大模型接口调用，有 Prompt Engineering 实践经验")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("了解向量数据库 (Pinecone/Milvus)、知识库构建流程")] }),

      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("通用技能")]
      }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("具备扎实的计算机科学基础（数据结构、算法、计算机网络）")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("良好的英文阅读能力，能流畅阅读英文技术文档")] }),
      new Paragraph({ numbering: { reference: "skills-bullets", level: 0 },
        children: [new TextRun("较强的学习能力和问题解决能力，对新技术保持高度敏感")] }),

      new Paragraph({ spacing: { after: 80 }, children: [] }),

      // ========== 项目经验 ==========
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "2E75B6", space: 1 } },
        children: [new TextRun("项目经验")]
      }),
      new Paragraph({ spacing: { after: 40 }, children: [] }),

      // 项目一
      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("图书馆座位预约管理系统（独立开发）")]
      }),
      new Table({
        width: { size: 8500, type: WidthType.DXA },
        columnWidths: [6000, 2500],
        rows: [new TableRow({
          children: [
            new TableCell({ borders: noBorder(), width: { size: 6000, type: WidthType.DXA },
              children: [new Paragraph({ children: [new TextRun({ text: "技术栈：Vue.js + Element UI + Spring Boot + MySQL + WebSocket", color: "555555", italics: true })] })] }),
            new TableCell({ borders: noBorder(), width: { size: 2500, type: WidthType.DXA },
              children: [new Paragraph({ alignment: AlignmentType.RIGHT, children: [new TextRun({ text: "[项目时间]", color: "888888" })] })] })
          ]
        })]
      }),
      new Paragraph({ spacing: { after: 30 }, children: [] }),

      new Paragraph({ numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("设计并实现完整的 B/S 架构系统，包含移动端（Vant）和管理后台（Element UI）双端界面")] }),
      new Paragraph({ numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("基于 WebSocket 实现座位实时状态同步功能，支持多人同时在线查看座位占用情况")] }),
      new Paragraph({ numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("设计积分规则引擎与违规惩罚机制，通过 AOP 切面实现操作日志自动记录与审计追踪")] }),
      new Paragraph({ numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("实现复杂的业务逻辑：预约冲突检测、签到签退、申诉处理、违规记录等完整工作流")] }),
      new Paragraph({ numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("开发数据统计模块，支持多维度数据可视化（预约趋势、座位利用率、违规分析等）")] }),
      new Paragraph({ numbering: { reference: "bullets", level: 0 },
        children: [new TextRun("采用 RESTful API 设计规范，实现前后端分离架构，提升系统可维护性与扩展性")] }),

      new Paragraph({ spacing: { after: 80 }, children: [] }),

      // ========== AI/Agent 学习与实践 ==========
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "2E75B6", space: 1 } },
        children: [new TextRun("AI/Agent 学习与实践")]
      }),
      new Paragraph({ spacing: { after: 40 }, children: [] }),

      new Paragraph({
        heading: HeadingLevel.HEADING_2,
        children: [new TextRun("个人 AI Agent Demo 项目")]
      }),
      new Table({
        width: { size: 8500, type: WidthType.DXA },
        columnWidths: [6000, 2500],
        rows: [new TableRow({
          children: [
            new TableCell({ borders: noBorder(), width: { size: 6000, type: WidthType.DXA },
              children: [new Paragraph({ children: [new TextRun({ text: "技术栈：Python + LangChain + OpenAI API + Streamlit", color: "555555", italics: true })] })] }),
            new TableCell({ borders: noBorder(), width: { size: 2500, type: WidthType.DXA },
              children: [new Paragraph({ alignment: AlignmentType.RIGHT, children: [new TextRun({ text: "[项目时间或进行中]", color: "888888" })] })] })
          ]
        })]
      }),
      new Paragraph({ spacing: { after: 30 }, children: [] }),

      new Paragraph({ numbering: { reference: "bullets2", level: 0 },
        children: [new TextRun("基于 LangChain 框架搭建智能问答 Agent，实现文档检索增强生成 (RAG) 功能")] }),
      new Paragraph({ numbering: { reference: "bullets2", level: 0 },
        children: [new TextRun("设计多轮对话记忆管理方案，支持上下文理解与长对话场景")] }),
      new Paragraph({ numbering: { reference: "bullets2", level: 0 },
        children: [new TextRun("实践 Prompt Engineering，优化 System Prompt 提升回答准确率与可控性")] }),
      new Paragraph({ numbering: { reference: "bullets2", level: 0 },
        children: [new TextRun("探索 Tool Use / Function Calling 能力，实现 Agent 与外部 API 的交互")] }),

      new Paragraph({ spacing: { after: 80 }, children: [] }),

      // ========== 自我评价 ==========
      new Paragraph({
        heading: HeadingLevel.HEADING_1,
        border: { bottom: { style: BorderStyle.SINGLE, size: 6, color: "2E75B6", space: 1 } },
        children: [new TextRun("自我评价")]
      }),
      new Paragraph({ spacing: { after: 40 }, children: [] }),

      new Paragraph({ indent: { left: 360 },
        children: [new TextRun("热爱 AI 技术，对 Agent/LLM 应用开发有浓厚兴趣和强烈的学习动力。具备扎实的全栈开发能力和工程化思维，能够快速学习并应用新技术。善于独立解决问题，有完整的项目落地经验。渴望加入 AI 团队，在实战中成长，为 AI 应用落地贡献力量。")] }),

      new Paragraph({ spacing: { after: 200 }, children: [] }),

      // ========== 致谢 ==========
      new Paragraph({
        alignment: AlignmentType.CENTER,
        children: [new TextRun({ text: "感谢您的时间，期待有机会进一步交流！", italics: true, color: "777777", size: 20 })]
      }),
    ]
  }]
});

// 辅助函数：创建信息单元格
function createInfoCell(text) {
  return new TableCell({
    borders: noBorder(),
    margins: { top: 40, bottom: 40, left: 80, right: 80 },
    shading: { fill: "F8F9FA", type: ShadingType.CLEAR },
    children: [new Paragraph({ alignment: AlignmentType.CENTER, children: [new TextRun({ text: text, size: 18, color: "555555" })] })]
  });
}

// 辅助函数：无边框
function noBorder() {
  return { top: { style: BorderStyle.NONE }, bottom: { style: BorderStyle.NONE },
           left: { style: BorderStyle.NONE }, right: { style: BorderStyle.NONE } };
}

// 生成文档
Packer.toBuffer(doc).then(buffer => {
  fs.writeFileSync("Agent_Engineer_Resume.docx", buffer);
  console.log("简历已生成：Agent_Engineer_Resume.docx");
});
