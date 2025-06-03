"use client";

import sanitizeHtml from "sanitize-html";

const TruncatedHtmlLabel = ({
  htmlContent,
  wordLimit,
}: {
  htmlContent: string;
  wordLimit: number;
}) => {
  const isTruncated = htmlContent.length > wordLimit;

  const content = isTruncated
    ? htmlContent.substring(0, wordLimit) + " ..."
    : htmlContent;

  return (
    <div className="px-4">
      <div
        className="prose prose-blue dark:prose-invert max-w-none"
        dangerouslySetInnerHTML={{ __html: content }}
        title={isTruncated ? sanitizeHtml(htmlContent, { allowedTags: [] }) : undefined}
      />
    </div>
  );
};

export default TruncatedHtmlLabel;
